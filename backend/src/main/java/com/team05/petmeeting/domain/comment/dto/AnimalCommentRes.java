package com.team05.petmeeting.domain.comment.dto;

import com.team05.petmeeting.domain.comment.entity.AnimalComment;

import java.time.LocalDateTime;

public record AnimalCommentRes(
        Long commentId,
        String content,
        Long feedId,
        LocalDateTime createdAt
) {
    public static AnimalCommentRes from(AnimalComment comment) {
        return new AnimalCommentRes(
                comment.getId(),
                comment.getContent(),
                comment.getAnimal().getId(),
                comment.getCreatedAt()
        );
    }
}
