package com.team05.petmeeting.domain.user.service;

import com.team05.petmeeting.domain.user.dto.login.LoginReq;
import com.team05.petmeeting.domain.user.dto.login.LoginResponse;
import com.team05.petmeeting.domain.user.dto.login.LoginResult;
import com.team05.petmeeting.domain.user.dto.signup.SignupReq;
import com.team05.petmeeting.domain.user.dto.signup.SignupRes;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.errorCode.UserErrorCode;
import com.team05.petmeeting.domain.user.refreshtoken.entity.RefreshToken;
import com.team05.petmeeting.domain.user.refreshtoken.repository.RefreshTokenRepository;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.exception.BusinessException;
import com.team05.petmeeting.global.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.team05.petmeeting.global.security.util.RefreshTokenUtil.REFRESH_TOKEN_COOKIE_NAME;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignupRes signup(SignupReq request) {
        // username 중복 체크
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 엔티티 생성
        User user = User.create(
                request.username(),
                encodedPassword,
                request.nickname(),
                request.realname()
        );

        // 저장
        User savedUser = userRepository.save(user);

        // 응답 생성
        return new SignupRes(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getNickname()
        );
    }

    public LoginResult login(LoginReq loginReq) {

        // 사용자 조회
        User user = userRepository.findByUsername(loginReq.username())
                .orElseThrow(
                        () -> new BusinessException(UserErrorCode.LOGIN_FAILED)
                );

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.LOGIN_FAILED);
        }

        // jwt access token 생성
        String accessToken = jwtUtil.createToken(user.getId(), List.of(user.getRole()));

        // refresh 토큰 생성 및 db 저장 -> redis 변경 검토
        UUID uuid = UUID.randomUUID();
        RefreshToken saved = RefreshToken.create(user, uuid);
        refreshTokenRepository.save(saved);

        // dto 반환
        return new LoginResult(
                uuid.toString(),
                new LoginResponse("Bearer", accessToken)
        );
    }

    public void logout(HttpServletRequest request) {

        extractRefreshToken(request) // Cookie로부터 refreshToken 추출
                .ifPresent(token -> {
                    UUID uuid = UUID.fromString(token);
                    refreshTokenRepository.deleteByToken(uuid); // db에서 리프레시 토큰 삭제
                });
    }

    private Optional<String> extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst();
    }
}