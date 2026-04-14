package com.team05.demo.domain.user.controller;

import com.team05.demo.domain.user.dto.signup.SignupRequest;
import com.team05.demo.domain.user.dto.signup.SignupResponse;
import com.team05.demo.domain.user.service.UserService;
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
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<RsData<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        SignupResponse response = userService.signup(signupRequest);

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

    // 프로필 사진 등록 & 변경

    // 닉네임 & 비밀번호 변경

    // 작성 글 목록 & 갯수 전달

    // 그동안 응원 누른 응원 수 , 동물 수
}