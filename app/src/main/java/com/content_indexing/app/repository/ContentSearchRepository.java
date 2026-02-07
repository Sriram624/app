package com.content_indexing.app.repository;

import com.content_indexing.app.entity.search.ContentSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ContentSearchRepository
        extends ElasticsearchRepository<ContentSearchDocument, String> {
}

