package com.team05.petmeeting.domain.animal.controller;

import com.team05.petmeeting.domain.comment.dto.AnimalCommentRes;
import com.team05.petmeeting.domain.comment.dto.CommentReq;
import com.team05.petmeeting.domain.comment.service.CommentService;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/animals")
public class AnimalCommentController {

    private final CommentService commentService;

    @Operation(summary = "동물 댓글 작성")
    @PostMapping("/{animalId}/comments")
    public ResponseEntity<AnimalCommentRes> createAnimalComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long animalId,
            @Valid @RequestBody CommentReq commentReq) {

        AnimalCommentRes res = commentService.createAnimalComment(userDetails.getUserId(), animalId, commentReq);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "동물 댓글 수정")
    @PatchMapping("/{animalId}/comments/{commentId}") // 수지는 보통 Patch나 Put 사용
    public ResponseEntity<AnimalCommentRes> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long animalId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentReq commentReq) {

        AnimalCommentRes res = commentService.updateAnimalComment(userDetails.getUserId(), commentId, commentReq);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "동물 댓글 삭제")
    @DeleteMapping("/{animalId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long animalId,
            @PathVariable Long commentId) {

        commentService.deleteAnimalComment(userDetails.getUserId(), commentId);
        return ResponseEntity.noContent().build();
    }
}


