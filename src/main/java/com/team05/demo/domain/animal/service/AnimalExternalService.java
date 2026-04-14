package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.client.AnimalApiClient;
import com.team05.demo.domain.animal.dto.external.AnimalApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// 동물 관련 외부 API와의 통신을 담당하는 서비스 클래스
@Service
public class AnimalExternalService {
    private final AnimalApiClient animalApiClient;

    public AnimalExternalService(AnimalApiClient animalApiClient) {
        this.animalApiClient = animalApiClient;
    }

    // 유기동물 목록을 API에서 조회하는 메서드
    public AnimalApiResponse fetchAnimals() {
        String url = animalApiClient.getAbandonmentUrl()
                + "?serviceKey=" + animalApiClient.getServiceKey()
                + "&pageNo=1"
                + "&numOfRows=10"
                + "&_type=" + animalApiClient.getReturnType();

        return RestClient.create()
                .get()
                .uri(url)
                .retrieve()
                .body(AnimalApiResponse.class);
    }
}
