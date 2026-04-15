package com.team05.petmeeting.animal;

import com.team05.petmeeting.domain.animal.client.AnimalApiClient;
import com.team05.petmeeting.domain.animal.config.AnimalApiProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnimalApiClientTest {

    @Test
    @DisplayName("유기동물 목록 조회 URL 생성")
    void t1() {
        // given
        AnimalApiProperties properties = new AnimalApiProperties();
        properties.setBaseUrl("https://apis.data.go.kr/1543061/abandonmentPublicService_v2");
        properties.setServiceKey("test-key");

        AnimalApiClient animalApiClient = new AnimalApiClient(properties);

        // when
        String result = animalApiClient.getAbandonmentUrl();

        // then
        assertEquals(
                "https://apis.data.go.kr/1543061/abandonmentPublicService_v2/abandonmentPublic_v2",
                result
        );
    }

    @Test
    @DisplayName("시도 조회 URL 생성")
    void t2() {
        // given
        AnimalApiProperties properties = new AnimalApiProperties();
        properties.setBaseUrl("https://apis.data.go.kr/1543061/abandonmentPublicService_v2");
        properties.setServiceKey("test-key");

        AnimalApiClient animalApiClient = new AnimalApiClient(properties);

        // when
        String result = animalApiClient.getSidoUrl();

        // then
        assertEquals(
                "https://apis.data.go.kr/1543061/abandonmentPublicService_v2/sido_v2",
                result
        );

    }

    @Test
    @DisplayName("품종 조회 URL을 생성")
    void t3() {
        // given
        AnimalApiProperties properties = new AnimalApiProperties();
        properties.setBaseUrl("https://apis.data.go.kr/1543061/abandonmentPublicService_v2");
        properties.setServiceKey("test-key");

        AnimalApiClient animalApiClient = new AnimalApiClient(properties);

        // when
        String result = animalApiClient.getKindUrl();

        // then
        assertEquals(
                "https://apis.data.go.kr/1543061/abandonmentPublicService_v2/kind_v2",
                result
        );
    }

    @Test
    @DisplayName("서비스키 반환")
    void getServiceKey() {
        // given
        AnimalApiProperties properties = new AnimalApiProperties();
        properties.setBaseUrl("https://apis.data.go.kr/1543061/abandonmentPublicService_v2");
        properties.setServiceKey("test-key");

        AnimalApiClient animalApiClient = new AnimalApiClient(properties);

        // when
        String result = animalApiClient.getServiceKey();

        // then
        assertEquals("test-key", result);
    }


}
