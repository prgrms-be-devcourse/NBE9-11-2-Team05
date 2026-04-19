package com.team05.petmeeting.domain.ads.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "meta.ads")
public class MetaAdsProperties {
    //토큰, 계정ID 등 설정값 관리
    private String accessToken;
    private String adAccountId;
    private String pageId;
    private String apiVersion;
}
