package com.team05.petmeeting.domain.feed.dto;

import com.team05.petmeeting.domain.feed.entity.Feed;
import com.team05.petmeeting.domain.feed.enums.FeedCategory;

import java.time.LocalDateTime;

public record FeedListRes(
        Long feedId,
        Long userId,
        String nickname,
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
                feed.getUser() != null ? feed.getUser().getNickname() : null,
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
