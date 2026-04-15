package com.team05.petmeeting.domain.feed.controller;

import com.team05.petmeeting.domain.comment.dto.CommentReq;
import com.team05.petmeeting.domain.comment.dto.FeedCommentRes;
import com.team05.petmeeting.domain.comment.service.CommentService;
import com.team05.petmeeting.domain.feed.dto.FeedLikeRes;
import com.team05.petmeeting.domain.feed.dto.FeedListRes;
import com.team05.petmeeting.domain.feed.dto.FeedRequest;
import com.team05.petmeeting.domain.feed.dto.FeedRes;
import com.team05.petmeeting.domain.feed.service.FeedLikeService;
import com.team05.petmeeting.domain.feed.service.FeedService;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final CommentService commentService;
    private final FeedService feedService;
    private final UserRepository userRepository;
    private final FeedLikeService feedLikeService;

    // 댓글 작성
    @Operation(summary = "피드 댓글 작성")
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<FeedCommentRes> createFeedComment(
            @PathVariable Long feedId,
            @Valid @RequestBody CommentReq commentReq) {
        FeedCommentRes res = commentService.createFeedComment(feedId, commentReq);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "피드 댓글 수정")
    @PutMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<FeedCommentRes> updateComment(@PathVariable Long commentId,
                                                        @Valid @RequestBody CommentReq commentReq) {
        FeedCommentRes res = commentService.updateFeedComment(commentId, commentReq);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "피드 댓글 삭제")
    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteFeedComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "피드 글 작성")
    @PostMapping
    public ResponseEntity<FeedRes> write(
            @RequestBody FeedRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow();
        FeedRes res = feedService.write(request, user);
        return ResponseEntity.status(201).body(res);
    }

    @Operation(summary = "피드 글 수정")
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedRes> modify(
            @PathVariable Long feedId,
            @RequestBody FeedRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow();
        FeedRes res = feedService.modify(feedId, request, user);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "피드 글 삭제")
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long feedId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        User user = userRepository.findById(userDetails.getUserId()).orElseThrow();
        feedService.delete(feedId, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "피드 상세 조회")
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedRes> getFeed(
            @PathVariable Long feedId
    ) {
        FeedRes res = feedService.getFeed(feedId);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "피드 목록 조회")
    @GetMapping
    public ResponseEntity<Page<FeedListRes>> getFeeds(
            Pageable pageable
    ) {
        Page<FeedListRes> feeds = feedService.getFeeds(pageable);
        return ResponseEntity.ok(feeds);
    }

    @Operation(summary = "피드 좋아요 토글")
    @PostMapping("/{feedId}/likes")
    public ResponseEntity<FeedLikeRes> toggleLike(
            @PathVariable Long feedId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow();
        FeedLikeRes res = feedLikeService.toggleLike(feedId, user);
        return ResponseEntity.ok(res);
    }

}
