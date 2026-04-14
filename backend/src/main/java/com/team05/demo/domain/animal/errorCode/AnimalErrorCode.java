package com.team05.demo.domain.animal.errorCode;

import com.team05.demo.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnimalErrorCode implements ErrorCode {

    // HttpStatus 객체 사용 및 코드에 식별자(A-) 부여
    ANIMAL_NOT_FOUND(HttpStatus.NOT_FOUND, "A-001", "존재하지 않는 유기동물입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "A-002", "잘못된 페이지 번호입니다"),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
