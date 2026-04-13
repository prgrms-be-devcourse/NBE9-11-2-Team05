package com.team05.demo.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team05.demo.domain.comment.entity.Comment;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommentRes (
    Long commentId,
    String content,
    Long feedId,    // 유기동물 댓글이면 null이 들어감
    Long animalId,
    LocalDateTime createdAt
) {
    public static CommentRes from (Comment comment){
        boolean isFeedComment = comment.getFeed()!=null;
        return new CommentRes(
                comment.getId(),
                comment.getContent(),
                comment.getFeed() != null ? comment.getFeed().getId() : null,
                comment.getAnimal() != null ? comment.getAnimal().getId() : null,
                comment.getCreatedAt()
        );
    }
}
