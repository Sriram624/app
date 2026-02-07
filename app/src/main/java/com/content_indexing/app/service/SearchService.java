// Java
package com.content_indexing.app.service;

import com.content_indexing.app.dto.ContentMetadataResponseDto;
import com.content_indexing.app.dto.SearchQueryDto;
import com.content_indexing.app.entity.search.ContentSearchDocument;
import com.content_indexing.app.repository.ContentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ContentSearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RedisService redisService;

    public List<ContentMetadataResponseDto> search(SearchQueryDto queryDto) {
        String cacheKey = "search:" + queryDto.hashCode();

        List<ContentMetadataResponseDto> cached =
                redisService.getList(cacheKey, ContentMetadataResponseDto.class);
        if (cached != null) {
            return cached;
        }

        Criteria criteria = new Criteria("title").matches(queryDto.getQuery());
        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(0, 10));

        SearchHits<ContentSearchDocument> hits =
                elasticsearchOperations.search(query, ContentSearchDocument.class);

        List<ContentMetadataResponseDto> results =
                hits.getSearchHits()
                        .stream()
                        .map(SearchHit::getContent)
                        .map(this::toDto)
                        .toList();

        redisService.setList(cacheKey, results, 300);
        return results;
    }

    public ContentMetadataResponseDto getByHash(String hash) {
        ContentSearchDocument doc = searchRepository.findById(hash)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        return toDto(doc);
    }

    private ContentMetadataResponseDto toDto(ContentSearchDocument doc) {
        ContentMetadataResponseDto dto = new ContentMetadataResponseDto();
        dto.setHash(doc.getHash());
        dto.setTitle(doc.getTitle());
        dto.setTags(doc.getTags());
        dto.setMirrors(doc.getMirrors());
        dto.setPopularity(doc.getPopularity());
        dto.setViews(redisService.getLong("views:" + doc.getHash()));
        return dto;
    }
}
