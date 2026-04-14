package com.team05.demo.domain.comment.controller;

import com.team05.demo.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController{

    private final CommentService commentService;

//    @PutMapping("/{commentId}")
//    public ResponseEntity<FeedCommentRes> updateComment(@PathVariable Long commentId,
//                                                        @Valid @RequestBody CommentReq commentReq){
//        FeedCommentRes res = commentService.updateComment(commentId, commentReq);
//        return ResponseEntity.ok(res);
//    }
//
//    @DeleteMapping("/{commentId}")
//    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
//        commentService.deleteComment(commentId);
//        return ResponseEntity.noContent().build();
//    }

}