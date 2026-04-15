package com.team05.petmeeting.domain.user.controller;

import com.team05.petmeeting.domain.user.dto.login.LoginRequest;
import com.team05.petmeeting.domain.user.dto.login.LoginResponse;
import com.team05.petmeeting.domain.user.dto.login.LoginResult;
import com.team05.petmeeting.domain.user.dto.signup.SignupRequest;
import com.team05.petmeeting.domain.user.dto.signup.SignupResponse;
import com.team05.petmeeting.domain.user.service.UserAuthService;
import com.team05.petmeeting.global.security.util.RefreshTokenUtil;
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
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        SignupResponse response = userAuthService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResult loginResult = userAuthService.login(loginRequest);

        // 리프레시토큰 설정
        refreshTokenUtil.add(response, loginResult.refreshToken());

        return ResponseEntity.ok(loginResult.loginResponse());
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userAuthService.logout(request);
        refreshTokenUtil.delete(response);

        return ResponseEntity.noContent().build();
    }

    // 리프레시 토큰 재발급

    // 탈퇴


}