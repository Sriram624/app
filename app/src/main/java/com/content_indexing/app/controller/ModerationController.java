package com.content_indexing.app.controller;



import com.content_indexing.app.dto.ReportAbuseDto;
import com.content_indexing.app.service.ModerationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService service;

    @PostMapping("/report")
    @RateLimiter(name = "apiRateLimiter")
    public ResponseEntity<?> report(@RequestBody ReportAbuseDto dto) {
        service.reportAbuse(dto);
        return ResponseEntity.ok().build();
    }
}
