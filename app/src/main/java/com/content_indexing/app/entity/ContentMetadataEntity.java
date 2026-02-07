package com.content_indexing.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "content_metadata")
@Data
public class ContentMetadataEntity {
    @Id
    private String hash;
    private String title;
    @ElementCollection
    private List<String> mirrors;
    @ElementCollection
    private List<String> tags;

    @Column(columnDefinition = "uuid")
    private UUID submitterId;

    private Instant createdAt = Instant.now();

    private double trustScore = 1.0;

    private double popularity = 0.0;
}
