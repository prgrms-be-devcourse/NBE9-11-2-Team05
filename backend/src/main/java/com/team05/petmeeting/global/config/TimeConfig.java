package com.team05.petmeeting.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

// 시스템의 현재 시간을 제공하는 Clock 빈을 등록하여, 테스트 시에 고정된 시간으로 설정할 수 있도록 합니다.
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
