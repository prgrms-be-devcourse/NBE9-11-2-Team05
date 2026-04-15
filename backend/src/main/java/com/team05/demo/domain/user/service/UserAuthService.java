package com.team05.demo.domain.user.service;

import com.team05.demo.domain.user.dto.login.LoginRequest;
import com.team05.demo.domain.user.dto.login.LoginResponse;
import com.team05.demo.domain.user.dto.login.LoginResult;
import com.team05.demo.domain.user.dto.signup.SignupRequest;
import com.team05.demo.domain.user.dto.signup.SignupResponse;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.domain.user.errorCode.UserErrorCode;
import com.team05.demo.domain.user.refreshtoken.entity.RefreshToken;
import com.team05.demo.domain.user.refreshtoken.repository.RefreshTokenRepository;
import com.team05.demo.domain.user.repository.UserRepository;
import com.team05.demo.global.exception.BusinessException;
import com.team05.demo.global.security.util.JwtUtil;
import java.util.List;
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

    public SignupResponse signup(SignupRequest request) {
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
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getNickname()
        );
    }

    public LoginResult login(LoginRequest loginRequest) {

        // 사용자 조회
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(
                        () -> new BusinessException(UserErrorCode.LOGIN_FAILED)
                );

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
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
}