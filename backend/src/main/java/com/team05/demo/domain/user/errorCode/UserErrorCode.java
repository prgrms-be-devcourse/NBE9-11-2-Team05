package com.team05.demo.domain.user.errorCode;

import com.team05.demo.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // HttpStatus 객체 사용 및 코드에 식별자(U-) 부여
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U-001", "로그인이 필요한 서비스입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "U-002", "아이디 또는 비밀번호가 올바르지 않습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U-003", "이미 사용 중인 id입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-004", "존재하지 않는 사용자입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

}
