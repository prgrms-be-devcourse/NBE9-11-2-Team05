package com.team05.petmeeting.animal;

import com.team05.petmeeting.domain.animal.dto.external.AnimalApiResponse;
import com.team05.petmeeting.domain.animal.dto.external.AnimalBody;
import com.team05.petmeeting.domain.animal.dto.external.AnimalItem;
import com.team05.petmeeting.domain.animal.dto.external.AnimalItems;
import com.team05.petmeeting.domain.animal.dto.external.AnimalResponse;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import com.team05.petmeeting.domain.animal.repository.SyncStateRepository;
import com.team05.petmeeting.domain.animal.service.AnimalExternalService;
import com.team05.petmeeting.domain.animal.service.AnimalSyncService;
import com.team05.petmeeting.domain.animal.service.InitialSyncProgressNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalSyncServiceTest {

    @Mock
    private AnimalExternalService animalExternalService;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private SyncStateRepository syncStateRepository;

    @Mock
    private InitialSyncProgressNotificationService initialSyncProgressNotificationService;

    private AnimalSyncService animalSyncService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-04-16T03:04:05Z"), ZoneId.of("Asia/Seoul"));
        animalSyncService = new AnimalSyncService(
                animalExternalService,
                animalRepository,
                syncStateRepository,
                initialSyncProgressNotificationService,
                fixedClock
        );
    }

    @Test
    @DisplayName("페이지 적재 시 desertionNo가 비었거나 중복이면 저장 대상에서 제외한다")
    void fetchAndInsertAnimals_filtersBlankAndDuplicateDesertionNo() {
        when(animalExternalService.fetchAnimals(eq(1), eq(10), any(), any()))
                .thenReturn(responseWithItems(
                        createAnimalItem("A-100"),
                        createAnimalItem("A-100"),
                        createAnimalItem(" "),
                        createAnimalItem("A-200")
                ));

        AnimalSyncService.SyncResult result = animalSyncService.fetchAndInsertAnimals(
                1,
                10,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                Integer.MAX_VALUE
        );

        ArgumentCaptor<List> animalsCaptor = ArgumentCaptor.forClass(List.class);
        verify(animalRepository).saveAll(animalsCaptor.capture());
        assertThat(animalsCaptor.getValue()).hasSize(2);
        assertThat(result.savedCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("업데이트 적재는 빈 페이지를 만날 때까지 조회한다")
    void fetchAndSaveAnimalsByUpdatedDate_stopsAtEmptyPage() {
        when(animalExternalService.fetchAnimalsByUpdatedDate(eq(1), eq(10), any(), any()))
                .thenReturn(responseWithItems(createAnimalItem("A-100")));
        when(animalExternalService.fetchAnimalsByUpdatedDate(eq(2), eq(10), any(), any()))
                .thenReturn(emptyResponse());

        animalSyncService.fetchAndSaveAnimalsByUpdatedDate(
                LocalDate.of(2026, 4, 14),
                LocalDate.of(2026, 4, 18),
                10
        );

        verify(animalExternalService).fetchAnimalsByUpdatedDate(
                1,
                10,
                LocalDate.of(2026, 4, 14),
                LocalDate.of(2026, 4, 18)
        );
        verify(animalExternalService).fetchAnimalsByUpdatedDate(
                2,
                10,
                LocalDate.of(2026, 4, 14),
                LocalDate.of(2026, 4, 18)
        );
    }

    @Test
    @DisplayName("초기 적재가 임계값을 넘지 않으면 진행 알림을 보내지 않는다")
    void fetchAndSaveMonthlyAnimalsFrom2008_doesNotNotifyWhenThresholdNotReached() {
        when(animalExternalService.fetchAnimals(eq(1), eq(2), any(), any()))
                .thenReturn(responseWithItems(createAnimalItem("A-100"), createAnimalItem("A-200")));
        when(animalExternalService.fetchAnimals(eq(2), eq(2), any(), any()))
                .thenReturn(emptyResponse());

        animalSyncService.fetchAndSaveMonthlyAnimalsFrom2008(2, 2);

        verify(initialSyncProgressNotificationService, never()).notifyMilestonesCrossed(any(Integer.class), any(Integer.class));
    }

    private AnimalApiResponse emptyResponse() {
        return responseWithItems();
    }

    private AnimalApiResponse responseWithItems(AnimalItem... items) {
        AnimalApiResponse apiResponse = new AnimalApiResponse();
        AnimalResponse response = new AnimalResponse();
        AnimalBody body = new AnimalBody();
        AnimalItems animalItems = new AnimalItems();

        ReflectionTestUtils.setField(animalItems, "item", List.of(items));
        ReflectionTestUtils.setField(body, "items", animalItems);
        ReflectionTestUtils.setField(response, "body", body);
        ReflectionTestUtils.setField(apiResponse, "response", response);

        return apiResponse;
    }

    private AnimalItem createAnimalItem(String desertionNo) {
        AnimalItem item = new AnimalItem();
        ReflectionTestUtils.setField(item, "desertionNo", desertionNo);
        ReflectionTestUtils.setField(item, "processState", "보호중");
        ReflectionTestUtils.setField(item, "noticeNo", "NOTICE-1");
        ReflectionTestUtils.setField(item, "noticeEdt", "20260420");
        ReflectionTestUtils.setField(item, "upKindNm", "개");
        ReflectionTestUtils.setField(item, "kindFullNm", "믹스견");
        ReflectionTestUtils.setField(item, "colorCd", "갈색");
        ReflectionTestUtils.setField(item, "age", "2024(년생)");
        ReflectionTestUtils.setField(item, "weight", "3(Kg)");
        ReflectionTestUtils.setField(item, "sexCd", "M");
        ReflectionTestUtils.setField(item, "popfile1", "https://example.com/1.jpg");
        ReflectionTestUtils.setField(item, "popfile2", "https://example.com/2.jpg");
        ReflectionTestUtils.setField(item, "specialMark", "활발함");
        ReflectionTestUtils.setField(item, "careNm", "테스트 보호소");
        ReflectionTestUtils.setField(item, "careTel", "010-0000-0000");
        ReflectionTestUtils.setField(item, "happenDt", "20260415");
        ReflectionTestUtils.setField(item, "happenPlace", "서울");
        ReflectionTestUtils.setField(item, "careAddr", "서울시 강남구");
        ReflectionTestUtils.setField(item, "orgNm", "강남구");
        ReflectionTestUtils.setField(item, "noticeSdt", "20260415");
        ReflectionTestUtils.setField(item, "officetel", "02-000-0000");
        ReflectionTestUtils.setField(item, "chargeNm", "담당자");
        ReflectionTestUtils.setField(item, "neuterYn", "N");
        ReflectionTestUtils.setField(item, "specialMark", "활발함");
        return item;
    }
}
