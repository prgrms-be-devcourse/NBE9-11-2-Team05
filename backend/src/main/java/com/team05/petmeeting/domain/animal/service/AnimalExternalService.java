package com.team05.petmeeting.domain.animal.service;

import com.team05.petmeeting.domain.animal.client.AnimalApiClient;
import com.team05.petmeeting.domain.animal.dto.external.AnimalApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 동물 관련 외부 API와의 통신을 담당하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class AnimalExternalService {
    private final AnimalApiClient animalApiClient;

    // 유기동물 목록을 API에서 조회하는 메서드
    public AnimalApiResponse fetchAnimals(int pageNo, int numOfRows) {
        return fetchAnimals(pageNo, numOfRows, null, null);
    }

    // 유기동물 목록을 API에서 조회하는 메서드 (날짜 범위 포함)
    public AnimalApiResponse fetchAnimals(
            int pageNo,
            int numOfRows,
            LocalDate bgnde,
            LocalDate endde
    ) {
        String url = animalApiClient.getAbandonmentUrl()
                + "?serviceKey=" + animalApiClient.getServiceKey()
                + "&pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&_type=" + animalApiClient.getReturnType();

        if (bgnde != null) {
            url += "&bgnde=" + bgnde.format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        if (endde != null) {
            url += "&endde=" + endde.format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        return RestClient.create()
                .get()
                .uri(url)
                .retrieve()
                .body(AnimalApiResponse.class);
    }

    // 유기동물 목록을 업데이트 날짜 기준으로 API에서 조회하는 메서드
    public AnimalApiResponse fetchAnimalsByUpdatedDate(
            int pageNo,
            int numOfRows,
            LocalDate bgupd,
            LocalDate enupd
    ) {
        String url = animalApiClient.getAbandonmentUrl()
                + "?serviceKey=" + animalApiClient.getServiceKey()
                + "&pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&_type=" + animalApiClient.getReturnType();

        if (bgupd != null) {
            url += "&bgupd=" + bgupd.format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        if (enupd != null) {
            url += "&enupd=" + enupd.format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        return RestClient.create()
                .get()
                .uri(url)
                .retrieve()
                .body(AnimalApiResponse.class);
    }
}
