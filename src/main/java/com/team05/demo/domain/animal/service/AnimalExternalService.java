package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.client.AnimalApiClient;
import com.team05.demo.domain.animal.dto.external.AnimalApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

// 동물 관련 외부 API와의 통신을 담당하는 서비스 클래스
@Service
public class AnimalExternalService {
    private static final Logger log = LoggerFactory.getLogger(AnimalExternalService.class); //상태 로그 기록을 위한 로거
    private static final int DEFAULT_PAGE_NO = 1; // API 요청 시 사용할 기본 페이지 번호
    private static final int DEFAULT_PAGE_SIZE = 10; // API 요청 시 사용할 기본 페이지 크기

    private final AnimalApiClient animalApiClient;
    //매트릭 수집 객체
    private final Counter externalRequestSuccessCounter; // 외부 API 요청 성공 횟수를 세는 카운터
    private final Counter externalRequestFailureCounter; // 외부 API 요청 실패 횟수를 세는 카운터
    private final Timer externalRequestTimer; // 외부 API 요청의 지연 시간을 측정하는 타이머
    private final DistributionSummary externalItemCountSummary; // 외부 API 요청당 반환된 항목 수를 기록하는 분포 요약

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

    public AnimalExternalService(AnimalApiClient animalApiClient, MeterRegistry meterRegistry) {
        this.animalApiClient = animalApiClient;
        //.builder() 메서드를 사용하여 카운터와 타이머, 분포 요약 객체를 생성하고 MeterRegistry에 등록한다.
        // 매트릭 사용 이유 - 운영 환경에서 외부 API 호출의 성공/실패 횟수, 지연 시간,
        // 반환된 항목 수 등을 모니터링하여 시스템의 상태를 파악하고 문제를 조기에 감지할 수 있다.
        this.externalRequestSuccessCounter = Counter.builder("animal.external.requests") //
                .description("Number of successful external animal API requests")
                .tag("endpoint", "abandonmentPublic_v2")
                .tag("result", "success")
                .register(meterRegistry);

        this.externalRequestFailureCounter = Counter.builder("animal.external.requests")
                .description("Number of failed external animal API requests")
                .tag("endpoint", "abandonmentPublic_v2")
                .tag("result", "failure")
                .register(meterRegistry);

        this.externalRequestTimer = Timer.builder("animal.external.request.duration")
                .description("Latency of external animal API requests")
                .tag("endpoint", "abandonmentPublic_v2")
                .register(meterRegistry);

        this.externalItemCountSummary = DistributionSummary.builder("animal.external.items.count")
                .description("Number of animal records returned by the external API")
                .baseUnit("items")
                .register(meterRegistry);
    }

}
