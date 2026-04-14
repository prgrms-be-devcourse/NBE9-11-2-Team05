package com.team05.demo.domain.feed.dto;

import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.enums.FeedCategory;

import java.time.LocalDateTime;

public record FeedRes (

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

){
    public FeedRes(Feed feed){
       this(
               feed.getId(),
               feed.getUser() != null ? feed.getUser().getId() : null, // null 체크
               feed.getCategory(),
               feed.getTitle(),
               feed.getContent(),
               feed.getImageUrl(),
               0,
               0,
               feed.getCreatedAt(),
               feed.getUpdatedAt()
       );
    }
}
