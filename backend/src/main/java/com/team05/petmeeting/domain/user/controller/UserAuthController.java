package com.team05.petmeeting.domain.user.controller;

import com.team05.petmeeting.domain.user.dto.login.LoginAndRefreshRes;
import com.team05.petmeeting.domain.user.dto.login.LoginAndRefreshResult;
import com.team05.petmeeting.domain.user.dto.login.LoginReq;
import com.team05.petmeeting.domain.user.dto.signup.SignupReq;
import com.team05.petmeeting.domain.user.dto.signup.SignupRes;
import com.team05.petmeeting.domain.user.service.UserAuthService;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import com.team05.petmeeting.global.security.util.RefreshTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<SignupRes> signup(
            @Valid @RequestBody SignupReq signupReq
    ) {
        SignupRes response = userAuthService.signup(signupReq);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginAndRefreshRes> login(
            @Valid @RequestBody LoginReq loginReq,
            HttpServletResponse response
    ) {
        LoginAndRefreshResult loginAndRefreshResult = userAuthService.login(loginReq);

        // 리프레시토큰 설정
        refreshTokenUtil.add(response, loginAndRefreshResult.refreshToken());

        return ResponseEntity.ok(loginAndRefreshResult.loginAndRefreshRes());
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
    @PostMapping("/refresh")
    public ResponseEntity<LoginAndRefreshRes> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        LoginAndRefreshResult result = userAuthService.refresh(request);

        // refresh token 재설정 (rotate)
        refreshTokenUtil.add(response, result.refreshToken());

        return ResponseEntity.ok(result.loginAndRefreshRes());
    }

    // id, pw 찾기
//    @PostMapping("/find-id/request")
//    public ResponseEntity<Void> requestFindId(
//            @RequestBody @Valid FindIdReq request
//    ) {
//        userAuthService.sendFindIdOtp(request.email());
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/find-id/verify")
//    public ResponseEntity<FindIdRes> verifyFindId(
//            @RequestBody @Valid VerifyCodeReq request
//    ) {
//        String username = userAuthService.verifyFindIdOtp(
//                request.email(),
//                request.code()
//        );
//        return ResponseEntity.ok(new FindIdRes(username));
//    }

    // 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse response
    ) {
        userAuthService.withdraw(userDetails.getUserId());

        // refresh token 쿠키 제거
        refreshTokenUtil.delete(response);

        return ResponseEntity.noContent().build();
    }


}