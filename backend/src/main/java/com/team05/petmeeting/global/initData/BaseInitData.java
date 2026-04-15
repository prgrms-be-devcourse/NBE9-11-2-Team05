package com.team05.petmeeting.global.initData;

import com.team05.petmeeting.domain.feed.repository.FeedRepository;
import com.team05.petmeeting.domain.feed.service.FeedService;
import com.team05.petmeeting.domain.user.dto.signup.SignupReq;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.domain.user.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    @Autowired
    @Lazy
    private BaseInitData self;
    private final FeedRepository feedRepository;
    private final FeedService feedService;

    private final UserRepository userRepository;
    private final UserAuthService userAuthService;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (userRepository.count() > 0) {
            return;
        }
        userAuthService.signup(new SignupReq("admin", "12345678Aa!", "admin_nickname", "홍길동"));
    }
}
