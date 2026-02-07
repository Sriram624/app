package com.content_indexing.app.dto;

import lombok.Data;

@Data
public class ReportAbuseDto {
    private String contentHash;
    private String reason;
}
