package com.team05.petmeeting.domain.user.errorCode;

import com.team05.petmeeting.global.exception.ErrorCode;
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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-004", "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U-005", "비밀번호가 일치하지 않습니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "U-006", "예전 비밀번호와 같습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "U-007", "이미 사용 중인 닉네임입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

}
