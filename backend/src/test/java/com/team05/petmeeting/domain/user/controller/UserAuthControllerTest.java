package com.team05.petmeeting.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team05.petmeeting.domain.user.dto.emailsignup.EmailSignupReq;
import com.team05.petmeeting.domain.user.dto.emailstart.EmailStartReq;
import com.team05.petmeeting.domain.user.dto.login.local.EmailLoginReq;
import com.team05.petmeeting.domain.user.service.MailService;
import com.team05.petmeeting.domain.user.service.OtpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private OtpService otpService;

    @Test
    @DisplayName("이메일 시작 - 신규 사용자")
    void start_email_new_user() throws Exception {
        EmailStartReq req = new EmailStartReq("new@test.com");

        mockMvc.perform(post("/api/v1/auth/email/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false))
                .andExpect(jsonPath("$.nextStep").value("SIGNUP_WITH_OTP"));
    }

    @Test
    @DisplayName("OTP 전송")
    void send_otp() throws Exception {
        EmailStartReq req = new EmailStartReq("test@test.com");

        mockMvc.perform(post("/api/v1/auth/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_user_not_found() throws Exception {
        EmailLoginReq req = new EmailLoginReq("notfound@test.com", "Testpassword12!");

        mockMvc.perform(post("/api/v1/auth/email/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void login_fail_wrong_password() throws Exception {

        // signup 먼저 (실제 플로우에서는 verifyToken 필요하지만 테스트 단순화)
        EmailSignupReq signup = new EmailSignupReq("fake-token", "Password123!", "닉네임", "홍길동");

        mockMvc.perform(post("/api/v1/auth/email/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        EmailLoginReq login = new EmailLoginReq("test@test.com", "WrongPassword12!");

        mockMvc.perform(post("/api/v1/auth/email/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 없음")
    void refresh_fail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }
}