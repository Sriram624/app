package com.content_indexing.app.repository;

import com.content_indexing.app.entity.ContentMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentMetadataRepository
        extends JpaRepository<ContentMetadataEntity, String> {
}

