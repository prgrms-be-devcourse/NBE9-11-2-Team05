package com.team05.demo.domain.user.controller;

import com.team05.demo.domain.user.dto.login.LoginRequest;
import com.team05.demo.domain.user.dto.login.LoginResponse;
import com.team05.demo.domain.user.dto.login.LoginResult;
import com.team05.demo.domain.user.dto.signup.SignupRequest;
import com.team05.demo.domain.user.dto.signup.SignupResponse;
import com.team05.demo.domain.user.service.UserAuthService;
import com.team05.demo.global.rsData.RsData;
import com.team05.demo.global.security.util.RefreshTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final RefreshTokenUtil refreshTokenUtil;

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
    @PostMapping("/login")
    public ResponseEntity<RsData<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResult loginResult = userAuthService.login(loginRequest);

        // 리프레시토큰 설정
        refreshTokenUtil.add(response, loginResult.refreshToken());

        return ResponseEntity.ok(
                new RsData<>(
                        "로그인 성공",
                        "SUCCESS",
                        loginResult.loginResponse()
                )
        );
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<RsData<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userAuthService.logout(request);
        refreshTokenUtil.delete(response);

        return ResponseEntity.ok(
                new RsData<>("로그아웃 성공", "SUCCESS")
        );
    }

    // 리프레시 토큰 재발급

    // 탈퇴


}