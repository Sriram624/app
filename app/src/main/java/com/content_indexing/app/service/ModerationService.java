package com.content_indexing.app.service;

import com.content_indexing.app.dto.ReportAbuseDto;
import com.content_indexing.app.entity.ContentMetadataEntity;
import com.content_indexing.app.repository.ContentMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final ContentMetadataRepository repository;
    private final KafkaTemplate<String, ContentMetadataEntity> kafkaTemplate;

    public void reportAbuse(ReportAbuseDto dto) {
        ContentMetadataEntity entity = repository.findById(dto.getContentHash()).orElseThrow(() -> new RuntimeException("Content not found"));
        // Logic for trust score adjustment (e.g., based on reason)
        entity.setTrustScore(entity.getTrustScore() - 0.1);  // Simple decrement
        repository.save(entity);

        kafkaTemplate.send("content-events", "ContentReported", entity);
    }

    public void handleReport(ContentMetadataEntity entity) {
        // Additional async moderation logic, e.g., if trustScore < 0.5, remove or de-rank
        if (entity.getTrustScore() < 0.5) {
            kafkaTemplate.send("content-events", "ContentRemoved", entity);
        }
    }
}
