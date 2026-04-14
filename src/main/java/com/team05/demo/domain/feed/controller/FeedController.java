package com.team05.demo.domain.feed.controller;

import com.team05.demo.domain.comment.dto.CommentReq;
import com.team05.demo.domain.comment.dto.CommentRes;
import com.team05.demo.domain.comment.service.CommentService;
import com.team05.demo.domain.feed.dto.FeedRequest;
import com.team05.demo.domain.feed.dto.FeedRes;
import com.team05.demo.domain.feed.service.FeedService;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final CommentService commentService;
    private final FeedService feedService;
    private final UserRepository userRepository;

    // 댓글 작성
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<CommentRes> createFeedComment(
            @PathVariable Long feedId,
            @Valid @RequestBody CommentReq commentReq){
        CommentRes res = commentService.createFeedComment(feedId, commentReq);
        return ResponseEntity.ok(res);
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
        FeedRes res = feedService.modify(feedId, request);
        return ResponseEntity.ok(res);
    }
}
