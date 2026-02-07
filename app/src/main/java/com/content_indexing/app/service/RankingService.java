package com.content_indexing.app.service;

import com.content_indexing.app.entity.ContentMetadataEntity;
import com.content_indexing.app.repository.ContentMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ContentMetadataRepository contentMetadataRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String VIEWS_KEY_PREFIX = "content:views:";

    public void incrementViews(String hash) {
        redisTemplate.opsForValue().increment(VIEWS_KEY_PREFIX + hash);
    }

    public long getViews(String hash) {
        String views = redisTemplate.opsForValue().get(VIEWS_KEY_PREFIX + hash);
        return views != null ? Long.parseLong(views) : 0L;
    }

    public void updatePopularity(String hash) {
        Optional<ContentMetadataEntity> entityOpt = contentMetadataRepository.findById(hash);
        if (entityOpt.isPresent()) {
            ContentMetadataEntity entity = entityOpt.get();
            long views = getViews(hash);
            double popularity = views * entity.getTrustScore();
            entity.setPopularity(popularity);
            contentMetadataRepository.save(entity);
        }
    }

    public void updateTrustScore(String hash, double newTrustScore) {
        Optional<ContentMetadataEntity> entityOpt = contentMetadataRepository.findById(hash);
        if (entityOpt.isPresent()) {
            ContentMetadataEntity entity = entityOpt.get();
            entity.setTrustScore(newTrustScore);
            contentMetadataRepository.save(entity);
            updatePopularity(hash);
        }
    }
}
