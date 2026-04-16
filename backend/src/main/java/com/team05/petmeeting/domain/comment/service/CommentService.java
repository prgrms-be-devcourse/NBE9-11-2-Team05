package com.team05.petmeeting.domain.comment.service;

import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.service.AnimalService;
import com.team05.petmeeting.domain.comment.dto.AnimalCommentRes;
import com.team05.petmeeting.domain.comment.dto.CommentReq;
import com.team05.petmeeting.domain.comment.dto.FeedCommentRes;
import com.team05.petmeeting.domain.comment.entity.AnimalComment;
import com.team05.petmeeting.domain.comment.entity.FeedComment;
import com.team05.petmeeting.domain.comment.errorCode.CommentErrorCode;
import com.team05.petmeeting.domain.comment.repository.AnimalCommentRepository;
import com.team05.petmeeting.domain.comment.repository.FeedCommentRepository;
import com.team05.petmeeting.domain.feed.entity.Feed;
import com.team05.petmeeting.domain.feed.service.FeedService;
import com.team05.petmeeting.global.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final FeedCommentRepository feedCommentRepository;
    private final AnimalCommentRepository animalCommentRepository;
    private final AnimalService animalService;
    private final FeedService feedService;

    @Transactional
    public AnimalCommentRes createAnimalComment(Long animalId, CommentReq commentReq) {
        Animal animal = animalService.findByAnimalId(animalId);
        AnimalComment comment = AnimalComment.create(animal, commentReq.content());
        AnimalComment savedComment = animalCommentRepository.save(comment);
        return AnimalCommentRes.from(savedComment);
    }

    @Transactional
    public FeedCommentRes createFeedComment(Long feedId, CommentReq commentReq) {
        Feed feed = feedService.findByFeedId(feedId);
        FeedComment comment = FeedComment.create(feed, commentReq.content());
        FeedComment savedComment = feedCommentRepository.save(comment);
        return FeedCommentRes.from(savedComment);
    }

    @Transactional
    public AnimalCommentRes updateAnimalComment(Long commentId, @Valid CommentReq commentReq) {
        AnimalComment comment = animalCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
        comment.updateContent(commentReq.content());
        return AnimalCommentRes.from(animalCommentRepository.save(comment));
    }

    @Transactional
    public FeedCommentRes updateFeedComment(Long commentId, @Valid CommentReq commentReq) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
        comment.updateContent(commentReq.content());
        return FeedCommentRes.from(feedCommentRepository.save(comment));
    }

    @Transactional
    public void deleteAnimalComment(Long commentId) {
        AnimalComment comment = animalCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
        animalCommentRepository.delete(comment);
    }

    @Transactional
    public void deleteFeedComment(Long commentId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
        feedCommentRepository.delete(comment);
    }

    public List<FeedCommentRes> getFeedComments(Long feedId) {
        Feed feed = feedService.findByFeedId(feedId);
        return feedCommentRepository.findByFeed(feed)
                .stream()
                .map(FeedCommentRes::from)
                .toList();
    }
}