package com.content_indexing.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContentMetadataCreateDto {
    private String title;
    private List<String> mirrors;
    private String hash;
    private List<String> tags;

}
