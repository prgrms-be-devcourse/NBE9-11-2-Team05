package com.team05.demo.domain.animal.controller;

import com.team05.demo.domain.comment.dto.CommentReq;
import com.team05.demo.domain.comment.dto.CommentRes;
import com.team05.demo.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/animals")
public class AnimalController {

    private final CommentService commentService;

    @PostMapping("/{animalId}/comments")
    public ResponseEntity<CommentRes> createAnimalComment(
            @PathVariable Long animalId,
            @Valid @RequestBody CommentReq commentReq
    ){
        CommentRes res = commentService.createAnimalComment(animalId, commentReq);
        return ResponseEntity.ok(res);
    }
}
