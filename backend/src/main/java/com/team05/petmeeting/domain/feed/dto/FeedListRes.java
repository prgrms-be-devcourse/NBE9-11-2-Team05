package com.team05.petmeeting.domain.feed.dto;

import com.team05.petmeeting.domain.feed.entity.Feed;
import com.team05.petmeeting.domain.feed.enums.FeedCategory;
import java.time.LocalDateTime;

public record FeedListRes(
        Long feedId,
        Long userId,
        Long animalId,
        String nickname,
        FeedCategory category,
        String title,
        String content,
        String imageUrl,
        int likeCount,
        boolean isLiked,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public FeedListRes(Feed feed, Long likeCount, Long commentCount, boolean isLiked) {
        this(
                feed.getId(),
                feed.getUser() != null ? feed.getUser().getId() : null,
                feed.getAnimal() != null ? feed.getAnimal().getId() : null, // null 체크
                feed.getUser() != null ? feed.getUser().getNickname() : null,
                feed.getCategory(),
                feed.getTitle(),
                feed.getContent(),
                feed.getImageUrl(),
                likeCount.intValue(),
                isLiked,
                commentCount.intValue(),
                feed.getCreatedAt(),
                feed.getUpdatedAt()
        );
    }
}
