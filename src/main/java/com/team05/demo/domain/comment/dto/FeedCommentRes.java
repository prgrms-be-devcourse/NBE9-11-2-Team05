package com.team05.demo.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team05.demo.domain.comment.entity.FeedComment;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FeedCommentRes(
    Long commentId,
    String content,
    Long feedId,
    LocalDateTime createdAt
) {
    public static FeedCommentRes from (FeedComment comment){
        return new FeedCommentRes(
                comment.getId(),
                comment.getContent(),
                comment.getFeed().getId(),
                comment.getCreatedAt()
        );
    }
}
