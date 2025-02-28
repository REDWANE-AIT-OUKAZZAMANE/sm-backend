package io.xhub.smwall.scheduler;

import io.xhub.smwall.client.MetaClient;
import io.xhub.smwall.config.MetaProperties;
import io.xhub.smwall.constants.CacheNames;
import io.xhub.smwall.constants.ProfileNames;
import io.xhub.smwall.constants.RegexPatterns;
import io.xhub.smwall.dto.meta.InstagramMediaDTO;
import io.xhub.smwall.mappers.meta.InstagramMediaMapper;
import io.xhub.smwall.service.MediaService;
import io.xhub.smwall.utlis.RegexUtils;
import io.xhub.smwall.utlis.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableAsync
@Profile(ProfileNames.META)
public class MetaScheduler {
    private final static String IG_MEDIA_REQUEST_FIELDS = "id,caption,media_type,media_url,thumbnail_url,permalink,timestamp,owner{id,username,profile_picture_url},children{media_url,media_type}";
    private final static String IG_HASHTAG_MEDIA_REQUEST_FIELDS = "id,caption,media_type,media_url,permalink,timestamp,children{media_url,media_type}";
    private final static String IG_MENTION_MEDIA_REQUEST_FIELDS = "id,caption,media_type,media_url,permalink,timestamp,children{media_url,media_type}";
    private final MetaProperties metaProperties;
    private final MediaService mediaService;
    private final MetaClient metaClient;
    private final Cache processedMediaCache;
    private final List<String> allowedMatches;
    private final InstagramMediaMapper instagramMediaMapper;

    public MetaScheduler(MetaProperties metaProperties, MediaService mediaService, MetaClient metaClient, CacheManager cacheManager, InstagramMediaMapper instagramMediaMapper) {
        this.metaProperties = metaProperties;
        this.mediaService = mediaService;
        this.metaClient = metaClient;
        this.processedMediaCache = cacheManager.getCache(CacheNames.PROCESSED_IG_MEDIA);
        this.instagramMediaMapper = instagramMediaMapper;
        this.allowedMatches = createAllowedMatchesList();
    }


    private List<String> createAllowedMatchesList() {
        List<String> hashtags = metaProperties.getHashtags().keySet().stream()
                .map(StringUtils::prependHashtag)
                .toList();
        String mention = StringUtils.prependAtSign(metaProperties.getUsername());
        List<String> allowedMatches = new ArrayList<>(hashtags);
        allowedMatches.add(mention);
        return allowedMatches;
    }

    @Async
    @Scheduled(
            fixedDelayString = "${application.webhooks.meta.scheduling.hashtag-media-interval}",
            timeUnit = TimeUnit.SECONDS
    )
    public void getIGHashtagRecentMediaTask() {
        try {
            log.info("Getting IG hashtag recent media");

            List<InstagramMediaDTO> newMedia = metaProperties.getHashtags()
                    .values()
                    .stream()
                    .map(igHashtagId -> metaClient.getIGHashtagRecentMedia(igHashtagId,
                            metaProperties.getUserId(),
                            IG_HASHTAG_MEDIA_REQUEST_FIELDS))
                    .flatMap(res -> res.getData().stream())
                    .filter(this::isNewIGMedia)
                    .peek(instagramMediaDTO -> {
                        if (instagramMediaDTO.getCaption() != null) {
                            instagramMediaDTO.getSourceTypes()
                                    .addAll(RegexUtils.findAllowedMatches(instagramMediaDTO.getCaption(), RegexPatterns.SOURCE_TYPES, allowedMatches));
                        }
                    })
                    .collect(Collectors.toList());
            if (!newMedia.isEmpty()) {
                log.info("Persisting {} new IG hashtag recent media", newMedia.size());
                mediaService.addAllMedia(instagramMediaMapper.toEntity(newMedia));
            }
        } catch (Exception e) {
            log.error("Error while fetching IG hashtag recent media: {}", e.getMessage());
        }
    }

    @Async
    @Scheduled(
            fixedDelayString = "${application.webhooks.meta.scheduling.user-media-interval}",
            timeUnit = TimeUnit.SECONDS
    )
    public void getIGUserMediaTask() {
        try {
            log.info("Getting IG user media");

            List<InstagramMediaDTO> newMedia = metaClient.getIGUserMedia(metaProperties.getUserId(),
                            IG_MEDIA_REQUEST_FIELDS)
                    .getData()
                    .stream()
                    .filter(this::isNewIGMedia)
                    .collect(Collectors.toList());

            if (!newMedia.isEmpty()) {
                log.info("Persisting {} new IG user media", newMedia.size());
                mediaService.addAllMedia(instagramMediaMapper.toEntity(newMedia));
            }
        } catch (Exception e) {
            log.error("Error while fetching IG user media: {}", e.getMessage());
        }
    }

    @Async
    @Scheduled(
            fixedDelayString = "${application.webhooks.meta.scheduling.user-tags-interval}",
            timeUnit = TimeUnit.SECONDS
    )
    public void getIGUserTagsTask() {
        try {
            log.info("Getting IG user tags");

            List<InstagramMediaDTO> newMedia = metaClient.getIGUserTags(metaProperties.getUserId(),
                            IG_MENTION_MEDIA_REQUEST_FIELDS)
                    .getData()
                    .stream()
                    .filter(this::isNewIGMedia)
                    .peek(instagramMediaDTO -> {
                        if (instagramMediaDTO.getCaption() != null) {
                            instagramMediaDTO.getSourceTypes()
                                    .addAll(RegexUtils.findAllowedMatches(instagramMediaDTO.getCaption(), RegexPatterns.SOURCE_TYPES, allowedMatches));
                        }
                    })
                    .collect(Collectors.toList());

            if (!newMedia.isEmpty()) {
                log.info("Persisting {} new IG user tags", newMedia.size());
                mediaService.addAllMedia(instagramMediaMapper.toEntity(newMedia));
            }
        } catch (Exception e) {
            log.error("Error while fetching IG user tags: {}", e.getMessage());
        }
    }

    @Async
    @Scheduled(
            fixedDelayString = "${application.webhooks.meta.scheduling.user-stories-interval}",
            timeUnit = TimeUnit.SECONDS
    )
    public void getIGUserStoriesTask() {
        try {
            log.info("Getting IG user stories");

            List<InstagramMediaDTO> newMedia = metaClient.getIGUserStories(metaProperties.getUserId(),
                            IG_MEDIA_REQUEST_FIELDS)
                    .getData()
                    .stream()
                    .filter(this::isNewIGMedia)
                    .collect(Collectors.toList());

            if (!newMedia.isEmpty()) {
                log.info("Persisting {} new IG user stories", newMedia.size());
                mediaService.addAllMedia(instagramMediaMapper.toEntity(newMedia));
            }
        } catch (Exception e) {
            log.error("Error while fetching IG user stories: {}", e.getMessage());
        }
    }

    private boolean isNewIGMedia(InstagramMediaDTO media) {
        return processedMediaCache != null && processedMediaCache.putIfAbsent(media.getId(), true) == null;
    }
}