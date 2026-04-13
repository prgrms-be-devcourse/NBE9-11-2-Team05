package com.team05.demo.domain.comment.service;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.service.AnimalService;
import com.team05.demo.domain.comment.dto.CommentReq;
import com.team05.demo.domain.comment.dto.CommentRes;
import com.team05.demo.domain.comment.entity.Comment;
import com.team05.demo.domain.comment.errorCode.CommentErrorCode;
import com.team05.demo.domain.comment.repository.CommentRepository;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.service.FeedService;
import com.team05.demo.global.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService{

    private final CommentRepository commentRepository;
    private final AnimalService animalService;
    private final FeedService feedService;

    @Transactional
    public CommentRes createAnimalComment(Long animalId, CommentReq commentReq){
        Animal animal = animalService.findByAnimalId(animalId);
        Comment comment = Comment.createAnimalComment(animal, commentReq.content());
        Comment savedComment = commentRepository.save(comment);
        return CommentRes.from(savedComment);
    }

    @Transactional
    public CommentRes createFeedComment(Long feedId, CommentReq commentReq){
        Feed feed = feedService.findByFeedId(feedId);
        Comment comment = Comment.createFeedComment(feed, commentReq.content());
        Comment savedComment = commentRepository.save(comment);
        return CommentRes.from(savedComment);
    }

    public CommentRes updateComment(Long commentId, @Valid CommentReq commentReq) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
        comment.updateContent(commentReq.content());
        return CommentRes.from(commentRepository.save(comment));
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
    }
}