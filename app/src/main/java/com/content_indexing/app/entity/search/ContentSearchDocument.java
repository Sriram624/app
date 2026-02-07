package com.content_indexing.app.entity.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(indexName = "content")
public class ContentSearchDocument {

    @Id
    private String hash;

    private String title;
    private List<String> tags;
    private List<String> mirrors;

    private double trustScore;
    private double popularity;
    private Instant createdAt;
}
