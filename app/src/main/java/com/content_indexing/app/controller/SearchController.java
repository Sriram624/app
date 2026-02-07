package com.content_indexing.app.controller;


import com.content_indexing.app.dto.ContentMetadataResponseDto;
import com.content_indexing.app.dto.SearchQueryDto;
import com.content_indexing.app.service.SearchService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService service;

    @PostMapping
    @RateLimiter(name = "apiRateLimiter")
    public List<ContentMetadataResponseDto> search(@RequestBody SearchQueryDto query) {
        return service.search(query);
    }
}
