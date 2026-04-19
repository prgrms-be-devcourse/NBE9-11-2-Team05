package com.team05.petmeeting.ads;

import com.team05.petmeeting.domain.ads.client.MetaAdsClient;
import com.team05.petmeeting.domain.ads.config.MetaAdsProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MetaAdsClientTest {

    @Test
    @DisplayName("Facebook 캠페인 생성 테스트")
    void createCampaign() {
        // given
        MetaAdsClient metaAdsClient = createMetaAdsClient();

        // when
        String response = metaAdsClient.createCampaign("test campaign");

        // then
        assertNotNull(response);
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    // 테스트용 객체 생성 - .env 파일에서 설정값 읽어오기
    private MetaAdsClient createMetaAdsClient() {
        MetaAdsProperties properties = new MetaAdsProperties();
        properties.setAccessToken(getRequiredConfig("META_ACCESS_TOKEN"));
        properties.setAdAccountId(getRequiredConfig("META_AD_ACCOUNT_ID"));
        properties.setPageId(getRequiredConfig("META_PAGE_ID"));
        properties.setApiVersion(getConfigOrDefault("META_API_VERSION", "v19.0"));

        return new MetaAdsClient(properties);
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