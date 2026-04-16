package com.team05.petmeeting.domain.feed.dto;

import com.team05.petmeeting.domain.comment.dto.FeedCommentRes;
import com.team05.petmeeting.domain.feed.entity.Feed;
import com.team05.petmeeting.domain.feed.enums.FeedCategory;

import java.time.LocalDateTime;
import java.util.List;

public record FeedRes(
        String nickname,
        Long feedId,
        Long userId,
        FeedCategory category,
        String title,
        String content,
        String imageUrl,
        int likeCount,
        int commentCount,
        List<FeedCommentRes> comments,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public FeedRes(Feed feed, int likeCount) {
        this(
                feed.getUser().getNickname(),
                feed.getId(),
                feed.getUser() != null ? feed.getUser().getId() : null, // null 체크
                feed.getCategory(),
                feed.getTitle(),
                feed.getContent(),
                feed.getImageUrl(),
                likeCount,
                feed.getComments().size(),
                feed.getComments().stream()
                        .map(FeedCommentRes::from)
                        .toList(),
                feed.getCreatedAt(),
                feed.getUpdatedAt()
        );
    }
}
