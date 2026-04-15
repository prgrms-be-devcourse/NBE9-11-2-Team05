package com.team05.demo.domain.feed.dto;

import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.enums.FeedCategory;

import java.time.LocalDateTime;

public record FeedListRes(
        Long feedId,
        Long userId,
        FeedCategory category,
        String title,
        String content,
        String imageUrl,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public FeedListRes(Feed feed, int likeCount) {
        this(
                feed.getId(),
                feed.getUser() != null ? feed.getUser().getId() : null,
                feed.getCategory(),
                feed.getTitle(),
                feed.getContent(),
                feed.getImageUrl(),
                likeCount,
                feed.getComments().size(),
                feed.getCreatedAt(),
                feed.getUpdatedAt()
        );
    }
}
