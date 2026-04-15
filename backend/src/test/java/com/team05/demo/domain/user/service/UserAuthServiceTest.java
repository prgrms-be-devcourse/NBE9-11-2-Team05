package com.team05.demo.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team05.demo.domain.user.dto.login.LoginRequest;
import com.team05.demo.domain.user.dto.login.LoginResult;
import com.team05.demo.domain.user.dto.signup.SignupRequest;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.domain.user.errorCode.UserErrorCode;
import com.team05.demo.domain.user.refreshtoken.repository.RefreshTokenRepository;
import com.team05.demo.domain.user.repository.UserRepository;
import com.team05.demo.global.exception.BusinessException;
import com.team05.demo.global.security.util.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserAuthServiceTest {

    @InjectMocks
    private UserAuthService userAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // =========================
    // 회원가입
    // =========================

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest("testId", "password", "닉네임", "홍길동");

        when(userRepository.existsByUsername("testId")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPw");

        User savedUser = User.create("testId", "encodedPw", "닉네임", "홍길동");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        var result = userAuthService.signup(request);

        // then
        assertThat(result.username()).isEqualTo("testId");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void signup_fail_duplicate_username() {
        // given
        SignupRequest request = new SignupRequest("testId", "pw", "닉네임", "홍길동");

        when(userRepository.existsByUsername("testId")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userAuthService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(UserErrorCode.DUPLICATE_USERNAME);
    }

    // =========================
    // 로그인
    // =========================

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("testId", "password");

        User user = User.create("testId", "encodedPw", "닉네임", "홍길동");

        when(userRepository.findByUsername("testId")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPw")).thenReturn(true);
        when(jwtUtil.createToken(any(), anyList())).thenReturn("accessToken");

        // when
        LoginResult result = userAuthService.login(request);

        // then
        assertThat(result.loginResponse().accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isNotNull();

        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_fail_user_not_found() {
        // given
        LoginRequest request = new LoginRequest("testId", "password");

        when(userRepository.findByUsername("testId")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userAuthService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(UserErrorCode.LOGIN_FAILED);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrong_password() {
        // given
        LoginRequest request = new LoginRequest("testId", "password");

        User user = User.create("testId", "encodedPw", "닉네임", "홍길동");

        when(userRepository.findByUsername("testId")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPw")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userAuthService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(UserErrorCode.LOGIN_FAILED);
    }
}