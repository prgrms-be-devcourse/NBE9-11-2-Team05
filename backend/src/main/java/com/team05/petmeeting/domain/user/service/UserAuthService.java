package com.team05.petmeeting.domain.user.service;

import static com.team05.petmeeting.global.security.util.RefreshTokenUtil.REFRESH_TOKEN_COOKIE_NAME;

import com.team05.petmeeting.domain.user.dto.login.LoginAndRefreshResponse;
import com.team05.petmeeting.domain.user.dto.login.LoginAndRefreshResult;
import com.team05.petmeeting.domain.user.dto.login.LoginReq;
import com.team05.petmeeting.domain.user.dto.signup.SignupReq;
import com.team05.petmeeting.domain.user.dto.signup.SignupRes;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.errorCode.UserErrorCode;
import com.team05.petmeeting.domain.user.refreshtoken.entity.RefreshToken;
import com.team05.petmeeting.domain.user.refreshtoken.repository.RefreshTokenRepository;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.exception.BusinessException;
import com.team05.petmeeting.global.security.errorCode.SecurityErrorCode;
import com.team05.petmeeting.global.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public LoginAndRefreshResult login(LoginReq loginReq) {

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
        String accessToken = jwtUtil.createToken(user.getId(), List.of(user.getRole().name()));

        // refresh 토큰 생성 및 db 저장 -> redis 변경 검토
        UUID uuid = UUID.randomUUID();
        RefreshToken saved = RefreshToken.create(user, uuid);
        refreshTokenRepository.save(saved);

        // dto 반환
        return new LoginAndRefreshResult(
                uuid.toString(),
                new LoginAndRefreshResponse("Bearer", accessToken)
        );
    }

    public void logout(HttpServletRequest request) {

        extractRefreshToken(request) // Cookie로부터 refreshToken 추출
                .ifPresent(token -> {
                    UUID uuid = UUID.fromString(token);
                    refreshTokenRepository.deleteByToken(uuid); // db에서 리프레시 토큰 삭제
                });
    }

    public LoginAndRefreshResult refresh(HttpServletRequest request) {

        // 1. 쿠키에서 refreshToken 추출
        String refreshToken = extractRefreshToken(request)
                .orElseThrow(() -> new BusinessException(SecurityErrorCode.INVALID_TOKEN)
                );

        // 2. DB 조회
        RefreshToken savedToken = refreshTokenRepository.findByToken(UUID.fromString(refreshToken))
                .orElseThrow(() -> new BusinessException(SecurityErrorCode.INVALID_TOKEN));

        // 리프레시 토큰의 유효 기간이 지났을 경우 예외 throw
        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(savedToken); // cleanup
            throw new BusinessException(SecurityErrorCode.INVALID_TOKEN);
        }

        User user = savedToken.getUser();

        // 3. access token 재발급
        String newAccessToken = jwtUtil.createToken(user.getId(), List.of(user.getRole().name()));

        // 4. refresh token rotate
        refreshTokenRepository.delete(savedToken);

        UUID uuid = UUID.randomUUID();
        RefreshToken saved = RefreshToken.create(user, uuid);
        refreshTokenRepository.save(saved);

        return new LoginAndRefreshResult(
                uuid.toString(),
                new LoginAndRefreshResponse("Bearer", newAccessToken)
        );
    }

    public void withdraw(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 1. refresh token 전체 삭제
        refreshTokenRepository.deleteAllByUser(user);

        // 2. 유저 삭제
        userRepository.delete(user);

        // soft delete 추후 고려
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