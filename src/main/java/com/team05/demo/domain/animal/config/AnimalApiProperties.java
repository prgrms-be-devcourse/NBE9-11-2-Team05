package com.team05.demo.domain.animal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "animal.api") //application-dev.yml에서 animal.api로 시작하는 프로퍼티를 매핑
public class AnimalApiProperties {
    private String baseUrl; // API의 기본 URL
    private String serviceKey; // API 인증에 필요한 서비스 키
}
