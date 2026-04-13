package com.team05.demo.animal;

import com.team05.demo.domain.animal.client.AnimalApiClient;
import com.team05.demo.domain.animal.config.AnimalApiProperties;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.domain.animal.service.AnimalExternalService;
import com.team05.demo.domain.animal.service.AnimalSyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class AnimalSyncServiceTest {

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    @DisplayName("외부 API에서 동물 데이터를 가져와 DB에 저장하는 테스트")
    void t1() {

        AnimalSyncService animalSyncService = createAnimalSyncService();

        animalSyncService.fetchAndSaveAnimals();

        assertTrue(animalRepository.count() > 0);
    }

    private AnimalSyncService createAnimalSyncService() {
        AnimalApiProperties properties = new AnimalApiProperties();
        properties.setBaseUrl(getRequiredConfig("ANIMAL_API_BASE_URL"));
        properties.setServiceKey(getRequiredConfig("ANIMAL_API_SERVICE_KEY"));
        properties.setReturnType(getConfigOrDefault("ANIMAL_API_RETURN_TYPE", "json"));

        AnimalApiClient animalApiClient = new AnimalApiClient(properties);
        AnimalExternalService animalExternalService = new AnimalExternalService(animalApiClient);

        return new AnimalSyncService(animalExternalService, animalRepository);
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
