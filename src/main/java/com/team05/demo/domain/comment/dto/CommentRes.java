package com.team05.demo.domain.comment.dto;

import com.team05.demo.domain.comment.entity.Comment;

public record CommentRes (
    Long commentId,
    String content,
    String targetType, // FEED or ANIMAL
    Long targetId
) {
    public static CommentRes from (Comment comment){
        boolean isFeedComment = comment.getFeed()!=null;
        return new CommentRes(
                comment.getId(),
                comment.getContent(),
                isFeedComment ? "FEED" : "ANIMAL",
                isFeedComment ? comment.getFeed().getId() : comment.getAnimal().getId()
        );
    }
}
