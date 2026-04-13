package com.team05.demo.domain.feed.controller;

import com.team05.demo.domain.comment.dto.CommentReq;
import com.team05.demo.domain.comment.dto.CommentRes;
import com.team05.demo.domain.comment.service.CommentService;
import com.team05.demo.domain.feed.dto.FeedRequest;
import com.team05.demo.domain.feed.dto.FeedRes;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.service.FeedService;
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

    // 댓글 작성
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<CommentRes> createFeedComment(
            @PathVariable Long feedId,
            @Valid @RequestBody CommentReq commentReq){
        CommentRes res = commentService.createAnimalComment(feedId, commentReq);
        return ResponseEntity.ok(res);
    }


    @PostMapping
    public ResponseEntity<FeedRes> write(@RequestBody FeedRequest request) {
        // JWT 구현 후 실제 user로 교체
        Feed feed = feedService.write(request, null);
        return ResponseEntity.status(201).body(new FeedRes(feed));
    }
}
