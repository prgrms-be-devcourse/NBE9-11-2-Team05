package com.team05.petmeeting.user.controller;

import com.team05.petmeeting.domain.user.controller.UserProfileController;
import com.team05.petmeeting.domain.user.dto.profile.UserCheerAnimalRes;
import com.team05.petmeeting.domain.user.service.UserProfileService;
import com.team05.petmeeting.global.security.filter.JwtAuthenticationFilter;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import com.team05.petmeeting.global.security.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@ActiveProfiles("test")
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserProfileService userProfileService;


    @Test
    @DisplayName("응원 동물 목록 조회 성공")
    void t1() throws Exception {
        // given
        CustomUserDetails mockUserDetails = new CustomUserDetails(1L, List.of());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, List.of());

        UserCheerAnimalRes mockRes = new UserCheerAnimalRes(2, List.of());
        given(userProfileService.getMyCheerAnimals(1L)).willReturn(mockRes);

        // when & then
        mockMvc.perform(get("/api/v1/me/cheer-animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(auth))) // 이렇게 주입!
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAnimalCount").value(2));
    }
}