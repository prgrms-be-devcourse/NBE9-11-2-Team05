package com.team05.demo.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team05.demo.domain.user.dto.login.LoginRequest;
import com.team05.demo.domain.user.dto.signup.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserAuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void signup() throws Exception {
        // 회원가입
        SignupRequest signup = new SignupRequest("testusername", "TestPassword12!", "닉네임", "홍길동");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("testusername"))
                .andExpect(jsonPath("$.data.nickname").value("닉네임"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 가입한 사용자")
    void signup_fail1() throws Exception {
        // given
        SignupRequest signup = new SignupRequest("duplicateUser", "TestPassword12!", "닉네임", "홍길동");

        // 1. 먼저 회원가입 수행 (DB에 저장됨)
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup))
                )
                .andExpect(status().isCreated());

        // 2. 동일한 username으로 다시 요청
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("U-003"));
    }

    @Test
    @DisplayName("회원가입 실패 - 입력값 검증 실패")
    void signup_fail2() throws Exception {
        // given
        SignupRequest signup = new SignupRequest("id", "wrongpassword", "", "");

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {

        SignupRequest signup = new SignupRequest("testusername", "TestPassword12!", "닉네임", "홍길동");

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup))
        );

        // 로그인
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지않는 id")
    void login_fail1() throws Exception {

        SignupRequest login = new SignupRequest("testusername", "TestPassword12!", "닉네임", "홍길동");

        // 로그인
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("U-002"));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 pw")
    void login_fail2() throws Exception {

        SignupRequest signup = new SignupRequest("testusername", "TestPassword12!", "닉네임", "홍길동");

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup))
        );

        LoginRequest login = new LoginRequest("testusername", "WrongPassword!!12");

        // 로그인
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("U-002"));
    }
}