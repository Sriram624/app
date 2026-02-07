package com.content_indexing.app.dto;

import lombok.Data;

@Data
public class SearchQueryDto {
    private String query;
    private String filters;
    private int page = 1;
    private int size = 10;
    private String sort = "relevance";
}
