package com.team05.petmeeting.global.initData;

import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
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

import java.time.LocalDate;

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

    // 최준 임시 작업
    private final AnimalRepository animalRepository;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            self.work1();
            self.work2();
        };
    }

    @Transactional
    public void work1() {
        if (userRepository.count() > 0) {
            return;
        }
        userAuthService.signup(new SignupReq("admin", "12345678Aa!", "admin_nickname", "홍길동"));
    }

    @Transactional
    public void work2() {
        if (animalRepository.count() > 0) {
            return;
        }
        Animal animal1 = new Animal(
                "D123456",
                "보호중",
                "N123",
                LocalDate.of(2026, 1, 10),
                "개",
                "믹스견",
                "갈색",
                "2020(년생)",
                "7kg",
                "M",
                "img1.jpg",
                "img2.jpg",
                "귀여움",
                "테스트 보호소1",
                "010-1234-5678",
                0
        );

        Animal animal2 = new Animal(
                "D333333",
                "보호중",
                "N333",
                LocalDate.of(2026, 3, 10),
                "고양이",
                "샴",
                "갈색",
                "2023(년생)",
                "4kg",
                "W",
                "img1.jpg",
                "img2.jpg",
                "도도함",
                "테스트 보호소2",
                "010-3333-1111",
                0
        );

        animalRepository.saveAndFlush(animal1);
        animalRepository.saveAndFlush(animal2);

    }


}
