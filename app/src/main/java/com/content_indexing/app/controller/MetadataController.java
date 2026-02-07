package com.content_indexing.app.controller;

import com.content_indexing.app.dto.ContentMetadataCreateDto;
import com.content_indexing.app.dto.ContentMetadataResponseDto;
import com.content_indexing.app.entity.ContentMetadataEntity;
import com.content_indexing.app.service.MetadataService;
import com.content_indexing.app.service.RankingService;
import com.content_indexing.app.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataService service;
    private final SearchService searchService;
    private final RankingService rankingService;
    private final KafkaTemplate<String, ContentMetadataEntity> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping
    @RateLimiter(name = "apiRateLimiter")
    public ResponseEntity<?> create(
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication auth
    ) throws IOException, NoSuchAlgorithmException {

        ContentMetadataCreateDto dto =
                objectMapper.readValue(metadataJson, ContentMetadataCreateDto.class);

        if (dto.getMirrors() == null) {
            dto.setMirrors(new ArrayList<>());
        }

        if (file != null && !file.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads", filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path);

            dto.getMirrors().add("http://localhost:8080/uploads/" + filename);

            if (dto.getHash() == null || dto.getHash().isBlank()) {
                dto.setHash(computeSha256(path.toFile()));
            }
        }

        if (dto.getHash() == null || dto.getHash().isBlank()) {
            throw new IllegalArgumentException("Hash is required if no file is uploaded");
        }

        service.createOrUpdate(dto, auth);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{hash}")
    @RateLimiter(name = "apiRateLimiter")
    public ContentMetadataResponseDto view(@PathVariable String hash) {
        ContentMetadataResponseDto dto = searchService.getByHash(hash);
        rankingService.incrementViews(hash);

        // Publish async view event
        ContentMetadataEntity entity = new ContentMetadataEntity();
        entity.setHash(hash);
        kafkaTemplate.send("content-events", "ContentViewed", entity);

        return dto;
    }

    private String computeSha256(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;

        try (var fis = Files.newInputStream(file.toPath())) {
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
