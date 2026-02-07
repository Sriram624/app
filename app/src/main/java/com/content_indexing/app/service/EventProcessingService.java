package com.content_indexing.app.service;
import com.content_indexing.app.entity.ContentMetadataEntity;
import com.content_indexing.app.repository.ContentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProcessingService {

    private final ContentSearchRepository searchRepository;
    private final RankingService rankingService;
    private final ModerationService moderationService;
    private final SearchMapper mapper;// Added for reports

    @KafkaListener(topics = "content-events", groupId = "event-processor")
    public void processEvent(String key, ContentMetadataEntity event) {

        switch (key) {
            case "ContentSubmitted", "ContentUpdated" ->
                    searchRepository.save(mapper.toDocument(event));

            case "ContentViewed" ->
                    rankingService.incrementViews(event.getHash());

            case "ContentReported" ->
                    moderationService.handleReport(event);

            case "ContentRemoved" ->
                    searchRepository.deleteById(event.getHash());
        }
    }

}
