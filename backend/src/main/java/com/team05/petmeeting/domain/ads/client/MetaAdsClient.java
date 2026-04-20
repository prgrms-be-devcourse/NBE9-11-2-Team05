package com.team05.petmeeting.domain.ads.client;

import com.team05.petmeeting.domain.ads.config.MetaAdsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class MetaAdsClient {
    //Facebook API 실제 호출
    private final MetaAdsProperties properties;

    private static final String BASE_URL = "https://graph.facebook.com";

    // 캠페인 생성
    public String createCampaign(String name) {
        String url = BASE_URL + "/" + properties.getApiVersion()
                + "/act_" + properties.getAdAccountId()
                + "/campaigns";

        String body = "name=" + name
                + "&objective=OUTCOME_TRAFFIC"
                + "&status=PAUSED"
                + "&special_ad_categories=NONE"
                + "&is_adset_budget_sharing_enabled=false"
                + "&access_token=" + properties.getAccessToken();

        try {
            return RestClient.create()
                    .post()
                    .uri(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw new IllegalStateException(
                    "Campaign 생성 실패: %s".formatted(e.getResponseBodyAsString()), e
            );
        }
    }
}
