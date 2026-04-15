package com.team05.demo.domain.user.controller;

import com.team05.demo.domain.user.dto.signup.SignupRequest;
import com.team05.demo.domain.user.dto.signup.SignupResponse;
import com.team05.demo.domain.user.service.UserAuthService;
import com.team05.demo.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class UserAuthController {

    private final UserAuthService userAuthService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<RsData<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        SignupResponse response = userAuthService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RsData<>(
                        "회원가입 성공",
                        "SUCCESS",
                        response
                ));
    }

    // 로그인

    // 로그아웃

    // 리프레시 토큰 재발급

    // 탈퇴

}