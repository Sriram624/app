package com.content_indexing.app.dto;

import lombok.Data;

import java.util.List;


@Data
public class ContentMetadataResponseDto{
    private String hash;
    private String title;
    private List<String> tags;
    private List<String> mirrors;
    private long views = 0;
    private double popularity = 0.0;
}
