package com.team05.petmeeting.ads;

import com.team05.petmeeting.domain.ads.client.MetaAdsClient;
import com.team05.petmeeting.domain.ads.config.MetaAdsProperties;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MetaAdsClientTest {

    private MetaAdsClient metaAdsClient;

    private static String campaignId;
    private static String adSetId;
    private static String creativeId;

    @BeforeEach
    void setUp() {
        MetaAdsProperties properties = new MetaAdsProperties();
        properties.setAccessToken(getRequiredConfig("META_ACCESS_TOKEN"));
        properties.setAdAccountId(getRequiredConfig("META_AD_ACCOUNT_ID"));
        properties.setPageId(getRequiredConfig("META_PAGE_ID"));
        properties.setApiVersion(getConfigOrDefault("META_API_VERSION", "v19.0"));
        metaAdsClient = new MetaAdsClient(properties);
    }

    @Test
    @Order(1)
    @DisplayName("Facebook 캠페인 생성 테스트")
    void createCampaign() {
        String response = metaAdsClient.createCampaign("test campaign");
        campaignId = extractId(response);
        assertNotNull(campaignId);
        System.out.println("캠페인 ID: " + campaignId);
    }

    @Test
    @Order(2)
    @DisplayName("Facebook AdSet 생성 테스트")
    void createAdSet() {
        String response = metaAdsClient.createAdSet("test adset", campaignId);
        adSetId = extractId(response);
        assertNotNull(adSetId);
        System.out.println("AdSet ID: " + adSetId);
    }

    @Test
    @Order(3)
    @DisplayName("Facebook Ad Creative 생성 테스트")
    void createAdCreative() {
        String response = metaAdsClient.createAdCreative("test creative", "테스트 광고입니다");
        creativeId = extractId(response);
        assertNotNull(creativeId);
        System.out.println("Creative ID: " + creativeId);
    }

    @Test
    @Order(4)
    @DisplayName("Facebook Ad 생성 테스트 (결제 수단 없으면 스킵)")
    void createAd() {
        try {
            String response = metaAdsClient.createAd("test ad", adSetId, creativeId);
            assertNotNull(response);
            System.out.println(response);
        } catch (IllegalStateException e) {
            // 결제 수단 없음 에러는 예상된 상황 → 테스트 통과 처리
            assertTrue(e.getMessage().contains("1359188"));
            System.out.println("결제 수단 없음 → 예상된 실패, 테스트 통과");
        }
    }

    // ====== 헬퍼 메서드 ======

    private String extractId(String response) {
        return response.replace("{\"id\":\"", "")
                .replace("\"}", "")
                .trim();
    }

    private String getRequiredConfig(String key) {
        String value = getConfig(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config: " + key);
        }
        return value;
    }

    private String getConfigOrDefault(String key, String defaultValue) {
        String value = getConfig(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private String getConfig(String key) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return readDotEnv().get(key);
    }

    private Map<String, String> readDotEnv() {
        Path path = Path.of(".env");
        if (!Files.exists(path)) {
            return Map.of();
        }
        try {
            return Files.readAllLines(path).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split("=", 2))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(
                            parts -> parts[0].trim(),
                            parts -> parts[1].trim()
                    ));
        } catch (IOException e) {
            throw new IllegalStateException(".env 파일 읽기 실패", e);
        }
    }
}