package com.team05.petmeeting.ads;

import com.team05.petmeeting.domain.ads.client.InstagramClient;
import com.team05.petmeeting.domain.ads.config.InstagramProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InstagramClientTest {

    private InstagramClient instagramClient;
    private static String containerId;

    @BeforeEach
    void setUp() {
        InstagramProperties properties = new InstagramProperties();
        properties.setUserId(getRequiredConfig("INSTAGRAM_USER_ID"));
        properties.setAccessToken(getRequiredConfig("INSTAGRAM_ACCESS_TOKEN"));
        instagramClient = new InstagramClient(properties);
    }

    @Test
    @Order(1)
    @DisplayName("Instagram 미디어 컨테이너 생성 테스트")
    void createMediaContainer() {
        String imageUrl = "https://images.dog.ceo/breeds/retriever-golden/n02099601_3004.jpg";
        String caption = "테스트 게시물입니다 🐾 #펫미팅 #유기동물 #입양";

        String response = instagramClient.createMediaContainer(imageUrl, caption);
        containerId = extractId(response);

        assertNotNull(containerId);
        System.out.println("컨테이너 ID: " + containerId);
    }

    @Test
    @Order(2)
    @DisplayName("Instagram 미디어 게시 테스트")
    void publishMedia() {
        String response = instagramClient.publishMedia(containerId);

        assertNotNull(response);
        assertFalse(response.isBlank());
        System.out.println("게시 결과: " + response);
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