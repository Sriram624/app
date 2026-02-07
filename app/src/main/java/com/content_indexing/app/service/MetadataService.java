package com.content_indexing.app.service;

import com.content_indexing.app.dto.ContentMetadataCreateDto;
import com.content_indexing.app.entity.ContentMetadataEntity;
import com.content_indexing.app.entity.UserEntity;
import com.content_indexing.app.repository.ContentMetadataRepository;
import com.content_indexing.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final ContentMetadataRepository repository;
    private final KafkaTemplate<String, ContentMetadataEntity> kafkaTemplate;
    private final UserRepository userRepository;

    public void createOrUpdate(ContentMetadataCreateDto dto, Authentication auth) {
        UserEntity user = userRepository
                .findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ContentMetadataEntity entity = repository.findById(dto.getHash()).orElse(new ContentMetadataEntity());
        entity.setHash(dto.getHash());
        entity.setTitle(dto.getTitle());
        entity.setTags(dto.getTags());
        entity.setMirrors(dto.getMirrors());
        entity.setSubmitterId(user.getId());
        repository.save(entity);
        kafkaTemplate.send("content-events", "ContentSubmitted", entity);


    }
}

