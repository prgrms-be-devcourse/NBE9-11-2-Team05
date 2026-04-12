package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.client.AnimalApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

// 동물 관련 외부 API와의 통신을 담당하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class AnimalExternalService {
    private final AnimalApiClient animalApiClient;

    // 유기동물 목록을 API에서 조회하는 메서드
    public String fetchAnimals() {
        String url = animalApiClient.getAbandonmentUrl()
                + "?serviceKey=" + animalApiClient.getServiceKey()
                + "&pageNo=1"
                + "&numOfRows=10"
                + "&_type=" + animalApiClient.getReturnType();
        String safeUrl = url.replace(animalApiClient.getServiceKey(), "***");

        try {
            return RestClient.create()
                    .get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw new IllegalStateException(
                    "Animal API call failed. url=%s, status=%s, response=%s"
                            .formatted(safeUrl, e.getStatusCode(), e.getResponseBodyAsString()),
                    e
            );
        }
    }

}
