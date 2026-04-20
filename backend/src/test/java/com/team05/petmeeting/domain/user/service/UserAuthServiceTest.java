package com.team05.petmeeting.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team05.petmeeting.domain.user.dto.emailsignup.EmailSignupReq;
import com.team05.petmeeting.domain.user.dto.login.LoginAndRefreshResult;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.entity.UserAuth;
import com.team05.petmeeting.domain.user.errorCode.UserErrorCode;
import com.team05.petmeeting.domain.user.provider.Provider;
import com.team05.petmeeting.domain.user.refreshtoken.repository.RefreshTokenRepository;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.exception.BusinessException;
import com.team05.petmeeting.global.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
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

    @Mock
    private OtpService otpService;

    @Mock
    private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 + 로그인 성공 (이메일 기반)")
    void signup_and_login_success() {
        // given
        String token = "valid-token";
        EmailSignupReq request = new EmailSignupReq(token, "password", "닉네임", "홍길동");

        when(otpService.getEmailByVerifyToken(token)).thenReturn(Optional.of("test@gmail.com"));
        when(passwordEncoder.encode("password")).thenReturn("encodedPw");

        User savedUser = User.create("test@gmail.com", "닉네임", "홍길동");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.createToken(any(), anyList())).thenReturn("accessToken");

        // when
        LoginAndRefreshResult result = userAuthService.signupAndLoginWithEmail(request);

        // then
        assertThat(result.accessTokenRes().accessToken()).isEqualTo("accessToken");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        String email = "test@gmail.com";
        String password = "password";

        User user = User.create(email, "닉네임", "홍길동");
        UserAuth auth = UserAuth.create(Provider.LOCAL, email, "encodedPw");
        user.addAuth(auth);

        when(userRepository.findByEmailWithAuths(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPw")).thenReturn(true);
        when(jwtUtil.createToken(any(), anyList())).thenReturn("accessToken");

        // when
        LoginAndRefreshResult result = userAuthService.loginWithEmail(email, password);

        // then
        assertThat(result.accessTokenRes().accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isNotNull();

        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_fail_user_not_found() {
        // given
        String email = "test@gmail.com";
        String password = "password";

        when(userRepository.findByEmailWithAuths(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userAuthService.loginWithEmail(email, password))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(UserErrorCode.LOGIN_FAILED);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrong_password() {
        // given
        String email = "test@gmail.com";
        String password = "password";

        User user = User.create(email, "닉네임", "홍길동");
        UserAuth auth = UserAuth.create(Provider.LOCAL, email, "encodedPw");
        user.addAuth(auth);

        when(userRepository.findByEmailWithAuths(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPw")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userAuthService.loginWithEmail(email, password))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(UserErrorCode.LOGIN_FAILED);
    }

    @Test
    @DisplayName("로그아웃 성공 - refreshToken 존재")
    void logout_success() {
        // given
        UUID token = UUID.randomUUID();

        Cookie cookie = new Cookie("refreshToken", token.toString());

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // when
        userAuthService.logout(request);

        // then
        verify(refreshTokenRepository).deleteByToken(token);
    }
}