package com.team05.demo.animal;

import com.team05.demo.domain.animal.dto.external.AnimalApiResponse;
import com.team05.demo.domain.animal.dto.external.AnimalBody;
import com.team05.demo.domain.animal.dto.external.AnimalItem;
import com.team05.demo.domain.animal.dto.external.AnimalItems;
import com.team05.demo.domain.animal.dto.external.AnimalResponse;
import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.domain.animal.service.AnimalExternalService;
import com.team05.demo.domain.animal.service.AnimalSyncService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnimalSyncServiceTest {

    @InjectMocks
    private AnimalSyncService animalSyncService;

    @Mock
    private AnimalExternalService animalExternalService;

    @Mock
    private AnimalRepository animalRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("같은 desertionNo가 있고 API 업데이트 시간이 더 최신이면 processState를 갱신한다")
    void fetchAndSaveAnimals_updatesProcessState_whenIncomingItemIsNewer() {
        // given
        AnimalItem existingItem = createItem("D123", "종결", "2024-08-11 10:00:00.0");
        Animal existingAnimal = Animal.from(createItem("D123", "보호중", "2024-08-10 10:00:00.0"));

        when(animalExternalService.fetchAnimals(1, 10)).thenReturn(createResponse(existingItem));
        when(animalRepository.findByDesertionNo("D123")).thenReturn(Optional.of(existingAnimal));

        // when
        int fetchedCount = animalSyncService.fetchAndSaveAnimals(1, 10);

        // then
        assertThat(fetchedCount).isEqualTo(1);
        assertThat(existingAnimal.getProcessState()).isEqualTo("종결");
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    @DisplayName("같은 desertionNo가 있어도 API 업데이트 시간이 더 오래되면 processState를 유지한다")
    void fetchAndSaveAnimals_keepsProcessState_whenIncomingItemIsOlder() {
        // given
        AnimalItem olderItem = createItem("D123", "종결", "2024-08-09 10:00:00.0");
        Animal existingAnimal = Animal.from(createItem("D123", "보호중", "2024-08-10 10:00:00.0"));

        when(animalExternalService.fetchAnimals(1, 10)).thenReturn(createResponse(olderItem));
        when(animalRepository.findByDesertionNo("D123")).thenReturn(Optional.of(existingAnimal));

        // when
        int fetchedCount = animalSyncService.fetchAndSaveAnimals(1, 10);

        // then
        assertThat(fetchedCount).isEqualTo(1);
        assertThat(existingAnimal.getProcessState()).isEqualTo("보호중");
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    @DisplayName("같은 desertionNo가 없으면 새 동물을 저장한다")
    void fetchAndSaveAnimals_savesAnimal_whenAnimalDoesNotExist() {
        // given
        AnimalItem newItem = createItem("D999", "보호중", "2024-08-10 10:00:00.0");

        when(animalExternalService.fetchAnimals(1, 10)).thenReturn(createResponse(newItem));
        when(animalRepository.findByDesertionNo("D999")).thenReturn(Optional.empty());

        // when
        int fetchedCount = animalSyncService.fetchAndSaveAnimals(1, 10);

        // then
        assertThat(fetchedCount).isEqualTo(1);
        verify(animalRepository).save(any(Animal.class));
    }

    private AnimalApiResponse createResponse(AnimalItem item) {
        AnimalItems items = new AnimalItems();
        ReflectionTestUtils.setField(items, "item", List.of(item));

        AnimalBody body = new AnimalBody();
        ReflectionTestUtils.setField(body, "items", items);

        AnimalResponse response = new AnimalResponse();
        ReflectionTestUtils.setField(response, "body", body);

        AnimalApiResponse apiResponse = new AnimalApiResponse();
        ReflectionTestUtils.setField(apiResponse, "response", response);

        return apiResponse;
    }

    private AnimalItem createItem(String desertionNo, String processState, String updTm) {
        AnimalItem item = new AnimalItem();
        ReflectionTestUtils.setField(item, "desertionNo", desertionNo);
        ReflectionTestUtils.setField(item, "processState", processState);
        ReflectionTestUtils.setField(item, "noticeNo", "N123");
        ReflectionTestUtils.setField(item, "noticeEdt", "20240810");
        ReflectionTestUtils.setField(item, "upKindNm", "개");
        ReflectionTestUtils.setField(item, "kindFullNm", "믹스견");
        ReflectionTestUtils.setField(item, "colorCd", "갈색");
        ReflectionTestUtils.setField(item, "age", "2020(년생)");
        ReflectionTestUtils.setField(item, "weight", "5kg");
        ReflectionTestUtils.setField(item, "sexCd", "M");
        ReflectionTestUtils.setField(item, "popfile1", "img1.jpg");
        ReflectionTestUtils.setField(item, "popfile2", "img2.jpg");
        ReflectionTestUtils.setField(item, "specialMark", "활발함");
        ReflectionTestUtils.setField(item, "careNm", "테스트 보호소");
        ReflectionTestUtils.setField(item, "careTel", "010-1234-5678");
        ReflectionTestUtils.setField(item, "updTm", updTm);
        return item;
    }
}
