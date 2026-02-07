package com.content_indexing.app.service;

import com.content_indexing.app.entity.ContentMetadataEntity;
import org.springframework.stereotype.Component;

@Component
public class SearchMapper {

    public com.content_indexing.app.entity.search.ContentSearchDocument toDocument(ContentMetadataEntity entity) {
        com.content_indexing.app.entity.search.ContentSearchDocument doc = new com.content_indexing.app.entity.search.ContentSearchDocument();
        doc.setHash(entity.getHash());
        doc.setTitle(entity.getTitle());
        doc.setTags(entity.getTags());
        doc.setMirrors(entity.getMirrors());
        doc.setTrustScore(entity.getTrustScore());
        doc.setPopularity(entity.getPopularity());
        doc.setCreatedAt(entity.getCreatedAt());
        return doc;
    }
}

