package com.content_indexing.app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableElasticsearchRepositories(
        basePackages = "com.content_indexing.app.repository"
)
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
    @PostConstruct
    public void init() {
        // Fixes the "Asia/Calcutta" vs "Asia/Kolkata" issue
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        // Alternatively, use UTC to avoid all daylight saving/region issues:
        // TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}

