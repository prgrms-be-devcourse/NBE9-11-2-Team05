package com.team05.demo.domain.feed.controller;

import com.team05.demo.domain.comment.dto.CommentReq;
import com.team05.demo.domain.comment.dto.FeedCommentRes;
import com.team05.demo.domain.comment.service.CommentService;
import com.team05.demo.domain.feed.dto.FeedListRes;
import com.team05.demo.domain.feed.dto.FeedRequest;
import com.team05.demo.domain.feed.dto.FeedRes;
import com.team05.demo.domain.feed.repository.FeedRepository;
import com.team05.demo.domain.feed.service.FeedService;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final CommentService commentService;
    private final FeedService feedService;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    // 댓글 작성
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<FeedCommentRes> createFeedComment(
            @PathVariable Long feedId,
            @Valid @RequestBody CommentReq commentReq){
        FeedCommentRes res = commentService.createFeedComment(feedId, commentReq);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<FeedCommentRes> updateComment(@PathVariable Long commentId,
                                                        @Valid @RequestBody CommentReq commentReq){
        FeedCommentRes res = commentService.updateFeedComment(commentId, commentReq);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
        commentService.deleteFeedComment(commentId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping
    public ResponseEntity<FeedRes> write(
            @RequestBody FeedRequest request) {
        // JWT 구현 후 실제 user로 교체
        User user = userRepository.findById(1L).orElseThrow();

        FeedRes res = feedService.write(request, user);
        return ResponseEntity.status(201).body(res);
    }

    @PutMapping("/{feedId}")
    public ResponseEntity<FeedRes> modify(
            @PathVariable Long feedId,
            @RequestBody FeedRequest request){

        // JWT 구현 후 실제 user로 교체
        User user = userRepository.findById(1L).orElseThrow();

        FeedRes res = feedService.modify(feedId, request, user);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> delete (
            @PathVariable Long feedId
    ){

        // JWT 구현 후 실제 user로 교체
        User user = userRepository.findById(1L).orElseThrow();

        feedService.delete(feedId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<FeedRes> getFeed(
            @PathVariable Long feedId
    ){
        FeedRes res = feedService.getFeed(feedId);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<Page<FeedListRes>> getFeeds(
            Pageable pageable
    ){
        Page<FeedListRes> feeds = feedService.getFeeds(pageable);
        return ResponseEntity.ok(feeds);
    }
}
