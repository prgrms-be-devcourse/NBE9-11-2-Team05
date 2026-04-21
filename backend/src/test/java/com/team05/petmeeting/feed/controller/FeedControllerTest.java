package com.team05.petmeeting.feed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team05.petmeeting.domain.comment.dto.CommentReq;
import com.team05.petmeeting.domain.feed.entity.Feed;
import com.team05.petmeeting.domain.feed.enums.FeedCategory;
import com.team05.petmeeting.domain.feed.repository.FeedRepository;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FeedControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    private Long feedId;
    private Long userId;

    @BeforeEach
    void setUp() {
        // 유저 먼저 저장
        User user = User.create("test@test.com", "테스터", "홍길동");
        user = userRepository.save(user);
        userId = user.getId();  // 실제 저장된 ID 사용

        // 피드 저장
        Feed feed = new Feed(user, FeedCategory.FREE, "테스트 피드", "내용", null, null);
        feed = feedRepository.save(feed);
        feedId = feed.getId();
    }

    @Test
    @DisplayName("피드 댓글 생성 성공")
    void createFeedComment() throws Exception {

        // @WithMockUser 대신 CustomUserDetails 직접 주입
        CustomUserDetails mockUserDetails = new CustomUserDetails(1L, List.of());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, List.of());

        CommentReq req = new CommentReq("테스트 댓글입니다.");

        mvc.perform(
                        post("/api/v1/feeds/{feedId}/comments", feedId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .with(authentication(auth))  // 인증 직접 주입
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("테스트 댓글입니다."))
                .andExpect(jsonPath("$.feedId").value(feedId));
    }
}