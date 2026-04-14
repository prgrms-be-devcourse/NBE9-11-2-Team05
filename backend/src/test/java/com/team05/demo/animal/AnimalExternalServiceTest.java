package com.team05.demo.animal;

import com.team05.demo.domain.animal.client.AnimalApiClient;
import com.team05.demo.domain.animal.config.AnimalApiProperties;
import com.team05.demo.domain.animal.dto.external.AnimalApiResponse;
import com.team05.demo.domain.animal.service.AnimalExternalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AnimalExternalServiceTest {
    @Test
    @DisplayName("외부 유기동물 목록 API 호출 테스트")
    void fetchAnimals() {
        // given
        AnimalExternalService animalExternalService = createAnimalExternalService();

        // when
        AnimalApiResponse response = animalExternalService.fetchAnimals();

        // then
        assertNotNull(response);
        assertNotNull(response.getResponse());
        assertNotNull(response.getResponse().getHeader());
        assertNotNull(response.getResponse().getBody());
        assertNotNull(response.getResponse().getBody().getItems());
        assertNotNull(response.getResponse().getBody().getItems().getItem());

        System.out.println(response.getResponse().getBody().getItems().getItem());
    }

    //테스트용 객체 생성 메서드 - 환경 변수 또는 .env 파일에서 설정을 읽어와 AnimalExternalService 객체를 생성
    private AnimalExternalService createAnimalExternalService() {
        AnimalApiProperties properties = new AnimalApiProperties();
        properties.setBaseUrl(getRequiredConfig("ANIMAL_API_BASE_URL"));
        properties.setServiceKey(getRequiredConfig("ANIMAL_API_SERVICE_KEY"));
        properties.setReturnType(getConfigOrDefault("ANIMAL_API_RETURN_TYPE", "json"));

        AnimalApiClient animalApiClient = new AnimalApiClient(properties);
        return new AnimalExternalService(animalApiClient);
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
            throw new IllegalStateException("Failed to read .env file", e);
        }
    }

}
