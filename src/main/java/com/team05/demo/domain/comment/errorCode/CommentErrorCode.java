package com.team05.demo.domain.comment.errorCode;

import com.team05.demo.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    // HttpStatus 객체 사용 및 코드에 식별자(C-) 부여
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C-001","로그인이 필요한 서비스입니다."),
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "C-002", "댓글은 2자 이상 작성해야합니다."),
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "C-003", "존재하지 않거나 이미 삭제된 피드입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C-004", "존재하지 않거나 이미 삭제된 댓글입니다."),
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
