package com.team05.demo.domain.comment.controller;

import com.team05.demo.domain.comment.dto.CommentReq;
import com.team05.demo.domain.comment.dto.CommentRes;
import com.team05.demo.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController{

    private final CommentService commentService;

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentRes> updateComment(@PathVariable Long commentId,
                                                    @Valid @RequestBody CommentReq commentReq){
        CommentRes res = commentService.updateComment(commentId, commentReq);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}