package com.team05.petmeeting.domain.user.service;

import static com.team05.petmeeting.global.security.util.RefreshTokenUtil.REFRESH_TOKEN_COOKIE_NAME;

import com.team05.petmeeting.domain.user.dto.login.LoginAndRefreshRes;
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
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String OTP_PREFIX = "otp:find-id:";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

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
                new LoginAndRefreshRes("Bearer", accessToken)
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
                new LoginAndRefreshRes("Bearer", newAccessToken)
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

    public void sendFindIdOtp(String email) {

        // 해당 이메일 회원이 없다고 알림
        userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException()
        );

        String code = generateOtp();

        redisTemplate.opsForValue()
                .set(OTP_PREFIX + email, code, 5, TimeUnit.MINUTES);

        // 이메일 발송

        //
    }

    public String verifyFindIdOtp(String email, String code) {
        String saved = redisTemplate.opsForValue()
                .get(OTP_PREFIX + email);

        // 해당 이메일에 대한 코드가 레디스에 존재하지않음  || 잘못된 인증 코드가 전송
        if (saved == null || !saved.equals(code)) {
            throw new BusinessException();
        }

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException()
        );

        redisTemplate.delete(OTP_PREFIX + email);

        // UserAuth에서 아이디를 가져와야할 것
        return maskUsername(user.getUsername());
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

    // 6자리 OTP 코드 생성 로직
    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // 사용자 id를 마스킹하여 반환
    private String maskUsername(String username) {
        int len = username.length();

        int maskLength = len - 4;

        return username.substring(0, 2)
                + "*".repeat(maskLength)
                + username.substring(len - 2);
    }
}