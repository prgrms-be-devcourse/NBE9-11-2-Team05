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

        // 1. URL 만들기
        String url = BASE_URL + "/" + properties.getApiVersion()
                + "/act_" + properties.getAdAccountId()
                + "/campaigns";

        // 2. Body 만들기
        String body = "name=" + name
                + "&objective=OUTCOME_TRAFFIC"
                + "&status=PAUSED"
                + "&special_ad_categories=NONE"
                + "&is_adset_budget_sharing_enabled=false"
                + "&access_token=" + properties.getAccessToken();

        try {
            // 3. API 호출
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
    // AdSet 생성
    public String createAdSet(String name, String campaignId) {
        String url = BASE_URL + "/" + properties.getApiVersion()
                + "/act_" + properties.getAdAccountId()
                + "/adsets";

        String body = "name=" + name
                + "&campaign_id=" + campaignId
                + "&daily_budget=200000"
                + "&billing_event=IMPRESSIONS"
                + "&optimization_goal=REACH"
                + "&targeting={\"geo_locations\":{\"countries\":[\"KR\"]}}"
                + "&bid_amount=100"
                + "&status=PAUSED"
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
                    "AdSet 생성 실패: %s".formatted(e.getResponseBodyAsString()), e
            );
        }
    }

    // Ad Creative 생성
    public String createAdCreative(String name, String message) {
        String url = BASE_URL + "/" + properties.getApiVersion()
                + "/act_" + properties.getAdAccountId()
                + "/adcreatives";

        String objectStorySpec = "{\"page_id\":\"" + properties.getPageId() + "\","
                + "\"link_data\":{\"link\":\"https://www.naver.com\","
                + "\"message\":\"" + message + "\"}}";

        String body = "name=" + name
                + "&object_story_spec=" + objectStorySpec
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
                    "Ad Creative 생성 실패: %s".formatted(e.getResponseBodyAsString()), e
            );
        }
    }

    // Ad 생성
    public String createAd(String name, String adSetId, String creativeId) {
        String url = BASE_URL + "/" + properties.getApiVersion()
                + "/act_" + properties.getAdAccountId()
                + "/ads";

        String body = "name=" + name
                + "&adset_id=" + adSetId
                + "&creative={\"creative_id\":\"" + creativeId + "\"}"
                + "&status=PAUSED"
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
                    "Ad 생성 실패: %s".formatted(e.getResponseBodyAsString()), e
            );
        }
    }
}
