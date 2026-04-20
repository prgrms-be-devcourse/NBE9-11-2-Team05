package com.team05.petmeeting.domain.animal.service;

import com.team05.petmeeting.domain.animal.config.AnimalSyncProperties;
import com.team05.petmeeting.domain.animal.dto.AnimalSyncResponse;
import com.team05.petmeeting.domain.animal.dto.external.AnimalApiResponse;
import com.team05.petmeeting.domain.animal.dto.external.AnimalItem;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.entity.AnimalSyncType;
import com.team05.petmeeting.domain.animal.entity.SyncState;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import com.team05.petmeeting.domain.animal.repository.SyncStateRepository;
import com.team05.petmeeting.domain.shelter.dto.ShelterCommand;
import com.team05.petmeeting.domain.shelter.service.ShelterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimalSyncService {
    private static final LocalDate INITIAL_SYNC_START_DATE = LocalDate.of(2008, 1, 1);
    private static final String SYNC_PAGE_MESSAGE = "유기동물 데이터 동기화 완료";
    private static final String INITIAL_SYNC_MESSAGE = "INITIAL_MONTHLY_SYNC_OK";
    private static final String UPDATE_SYNC_MESSAGE = "UPDATE_SYNC_OK";

    private final AnimalExternalService animalExternalService;
    private final AnimalRepository animalRepository;
    private final SyncStateRepository syncStateRepository;
    private final AnimalSyncProperties animalSyncProperties;
    private final ShelterService shelterService;

    // 특정 페이지를 수동으로 조회해 신규 동물만 저장하는 진입점
    @Transactional
    public AnimalSyncResponse fetchAndSaveAnimals(int pageNo, int numOfRows) {
        Instant startedAt = Instant.now();
        int savedCount = syncPage(pageNo, numOfRows, null, null, Integer.MAX_VALUE);
        return createResponse(SYNC_PAGE_MESSAGE, savedCount, startedAt);
    }

    // 2008년 1월부터 현재까지 월 단위로 순회하며 초기 적재를 수행한다
    @Transactional
    public AnimalSyncResponse runInitialMonthlySync(int numOfRows) {
        Instant startedAt = Instant.now();
        int savedCount = syncInitialMonthlyRange(numOfRows);
        updateSyncState(AnimalSyncType.INITIAL);
        return createResponse(INITIAL_SYNC_MESSAGE, savedCount, startedAt);
    }

    // 마지막 성공 동기화 시각 이후 수정된 데이터만 조회해 반영한다
    @Transactional
    public AnimalSyncResponse runUpdateSync(int numOfRows) {
        Instant startedAt = Instant.now();
        int savedCount = syncUpdatedAnimals(numOfRows);
        updateSyncState(AnimalSyncType.UPDATE);
        return createResponse(UPDATE_SYNC_MESSAGE, savedCount, startedAt);
    }

    // 초기 적재 기간을 월 단위 구간으로 나눠 각 월의 데이터를 끝 페이지까지 저장한다
    private int syncInitialMonthlyRange(int numOfRows) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = INITIAL_SYNC_START_DATE.withDayOfMonth(1);
        int totalSavedCount = 0;

        while (!monthStart.isAfter(today)) {
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
            if (monthEnd.isAfter(today)) {
                monthEnd = today;
            }

            totalSavedCount += syncDateRange(monthStart, monthEnd, numOfRows);
            monthStart = monthStart.plusMonths(1).withDayOfMonth(1);
        }

        return totalSavedCount;
    }

    // 한 달 범위를 페이지네이션으로 모두 조회하며 신규 데이터만 누적 저장한다
    private int syncDateRange(LocalDate startDate, LocalDate endDate, int numOfRows) {
        int pageNo = 1;
        int totalSavedCount = 0;

        while (true) {
            Instant startedAt = Instant.now();
            AnimalApiResponse response = animalExternalService.fetchAnimals(pageNo, numOfRows, startDate, endDate);
            List<AnimalItem> items = extractItems(response);
            if (items.isEmpty()) {
                log.info(
                        "Animal sync page completed: pageNo={}, numOfRows={}, bgnde={}, endde={}, savedCount=0, elapsedMs={}",
                        pageNo,
                        numOfRows,
                        startDate,
                        endDate,
                        elapsedMs(startedAt)
                );
                break;
            }

            int savedCount = saveNewAnimals(items, Integer.MAX_VALUE);
            log.info(
                    "Animal sync page completed: pageNo={}, numOfRows={}, bgnde={}, endde={}, savedCount={}, elapsedMs={}",
                    pageNo,
                    numOfRows,
                    startDate,
                    endDate,
                    savedCount,
                    elapsedMs(startedAt)
            );
            totalSavedCount += savedCount;
            pageNo++;
        }

        return totalSavedCount;
    }

    // 수정일 범위로 조회한 데이터를 기존 데이터와 비교해 insert 또는 update 한다
    private int syncUpdatedAnimals(int numOfRows) {
        LocalDate startDate = getUpdateStartDate();
        LocalDate endDate = LocalDate.now();
        int pageNo = 1;
        int totalSavedCount = 0;

        while (true) {
            Instant startedAt = Instant.now();
            AnimalApiResponse response = animalExternalService.fetchAnimalsByUpdatedDate(pageNo, numOfRows, startDate, endDate);
            List<AnimalItem> items = extractItems(response);

            if (items.isEmpty()) {
                log.info(
                        "Animal update sync page completed: pageNo={}, numOfRows={}, bgupd={}, enupd={}, savedCount=0, elapsedMs={}",
                        pageNo,
                        numOfRows,
                        startDate,
                        endDate,
                        elapsedMs(startedAt)
                );
                break;
            }

            int savedCount = saveOrUpdateAnimals(items);
            totalSavedCount += savedCount;

            log.info(
                    "Animal update sync page completed: pageNo={}, numOfRows={}, bgupd={}, enupd={}, savedCount={}, elapsedMs={}",
                    pageNo,
                    numOfRows,
                    startDate,
                    endDate,
                    savedCount,
                    elapsedMs(startedAt)
            );

            pageNo++;
            waitForNextUpdatePage();
        }

        return totalSavedCount;
    }

    // 일반 동기화용 페이지 조회 후 신규 저장 건수만 반환한다
    private int syncPage(int pageNo, int numOfRows, LocalDate startDate, LocalDate endDate, int maxSaveCount) {
        Instant startedAt = Instant.now();
        AnimalApiResponse response = animalExternalService.fetchAnimals(pageNo, numOfRows, startDate, endDate);
        List<AnimalItem> items = extractItems(response);

        if (items.isEmpty()) {
            log.info(
                    "Animal sync page completed: pageNo={}, numOfRows={}, bgnde={}, endde={}, savedCount=0, elapsedMs={}",
                    pageNo,
                    numOfRows,
                    startDate,
                    endDate,
                    elapsedMs(startedAt)
            );
            return 0;
        }

        int savedCount = saveNewAnimals(items, maxSaveCount);
        log.info(
                "Animal sync page completed: pageNo={}, numOfRows={}, bgnde={}, endde={}, savedCount={}, elapsedMs={}",
                pageNo,
                numOfRows,
                startDate,
                endDate,
                savedCount,
                elapsedMs(startedAt)
        );
        return savedCount;
    }

    // 외부 API 응답에서 실제 동물 목록만 안전하게 꺼낸다
    private List<AnimalItem> extractItems(AnimalApiResponse response) {
        if (response == null
                || response.getResponse() == null
                || response.getResponse().getBody() == null
                || response.getResponse().getBody().getItems() == null
                || response.getResponse().getBody().getItems().getItem() == null) {
            return List.of();
        }

        return response.getResponse().getBody().getItems().getItem();
    }

    // 신규 적재 시에는 desertionNo 중복을 제거하고 DB에 없는 데이터만 저장한다
    private int saveNewAnimals(List<AnimalItem> items, int maxSaveCount) {
        syncShelters(items);

        List<AnimalItem> uniqueItems = filterValidUniqueItems(items);
        Map<String, Animal> existingAnimals = findExistingAnimals(uniqueItems);
        List<Animal> animalsToSave = new ArrayList<>();

        for (AnimalItem item : uniqueItems) {
            if (animalsToSave.size() >= maxSaveCount) {
                break;
            }

            if (existingAnimals.containsKey(item.getDesertionNo())) {
                continue;
            }

            animalsToSave.add(Animal.from(item));
        }

        animalRepository.saveAll(animalsToSave);
        return animalsToSave.size();
    }

    // 업데이트 동기화 시에는 기존 데이터는 수정하고 없는 데이터는 새로 저장한다
    private int saveOrUpdateAnimals(List<AnimalItem> items) {
        syncShelters(items);

        List<AnimalItem> uniqueItems = filterValidUniqueItems(items);
        Map<String, Animal> existingAnimals = findExistingAnimals(uniqueItems);
        List<Animal> animalsToInsert = new ArrayList<>();
        int affectedCount = 0;

        for (AnimalItem item : uniqueItems) {
            Animal existingAnimal = existingAnimals.get(item.getDesertionNo());

            if (existingAnimal == null) {
                animalsToInsert.add(Animal.from(item));
                affectedCount++;
                continue;
            }

            if (!existingAnimal.needsUpdateFrom(item)) {
                continue;
            }

            existingAnimal.updateFrom(item);
            affectedCount++;
        }

        animalRepository.saveAll(animalsToInsert);
        return affectedCount;
    }

    private void syncShelters(List<AnimalItem> items) {
        // 보호소 먼저 저장
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        List<ShelterCommand> shelterCmds = items.stream()
                .map(item -> new ShelterCommand(
                        item.getCareRegNo(),
                        item.getCareNm(),
                        item.getCareTel(),
                        item.getCareAddr(),
                        item.getCareOwerNm(),
                        item.getOrgNm(),
                        LocalDateTime.parse(item.getUpdTm(), formatter)
                ))
                .distinct()  // 중복 제거
                .toList();

        shelterService.createOrUpdateShelters(shelterCmds);
    }

    // apiUpdatedAt 또는 주요 필드 차이를 기준으로 실제 수정이 필요한 경우만 반영한다
    private List<AnimalItem> filterValidUniqueItems(List<AnimalItem> items) {
        List<AnimalItem> uniqueItems = new ArrayList<>();
        Set<String> seenDesertionNos = new HashSet<>();

        for (AnimalItem item : items) {
            String desertionNo = item.getDesertionNo();
            if (desertionNo == null || desertionNo.isBlank()) {
                continue;
            }

            if (!seenDesertionNos.add(desertionNo)) {
                continue;
            }

            uniqueItems.add(item);
        }

        return uniqueItems;
    }

    // 요청 배치에 포함된 desertionNo를 한 번에 조회해 insert/update 분기에 사용한다
    private Map<String, Animal> findExistingAnimals(List<AnimalItem> items) {
        Set<String> desertionNos = new HashSet<>();

        for (AnimalItem item : items) {
            desertionNos.add(item.getDesertionNo());
        }

        if (desertionNos.isEmpty()) {
            return Map.of();
        }

        Map<String, Animal> animalMap = new HashMap<>();
        for (Animal animal : animalRepository.findAllByDesertionNoIn(desertionNos)) {
            animalMap.put(animal.getDesertionNo(), animal);
        }
        return animalMap;
    }

    // 마지막 성공 시각이 없으면 초기 기준일을, 있으면 가장 최근 성공 시각의 날짜를 반환한다
    private LocalDate getUpdateStartDate() {
        LocalDateTime initialSyncedAt = syncStateRepository.findBySyncType(AnimalSyncType.INITIAL)
                .map(SyncState::getLastUpdatedAt)
                .orElse(null);

        LocalDateTime updateSyncedAt = syncStateRepository.findBySyncType(AnimalSyncType.UPDATE)
                .map(SyncState::getLastUpdatedAt)
                .orElse(null);

        LocalDateTime latestSyncedAt = null;
        if (initialSyncedAt != null) {
            latestSyncedAt = initialSyncedAt;
        }
        if (updateSyncedAt != null && (latestSyncedAt == null || updateSyncedAt.isAfter(latestSyncedAt))) {
            latestSyncedAt = updateSyncedAt;
        }

        if (latestSyncedAt == null) {
            return INITIAL_SYNC_START_DATE;
        }

        return latestSyncedAt.toLocalDate();
    }

    // 동기화가 정상 끝난 경우에만 종류별 마지막 성공 시각을 갱신한다
    private void updateSyncState(AnimalSyncType syncType) {
        SyncState syncState = syncStateRepository.findBySyncType(syncType)
                .orElseGet(() -> SyncState.create(syncType));

        syncState.updateLastUpdatedAt(LocalDateTime.now());
        syncStateRepository.save(syncState);
    }

    // 공공 API 연속 호출 부담을 줄이기 위해 업데이트 페이지 간 짧게 대기한다
    private void waitForNextUpdatePage() {
        try {
            Thread.sleep(animalSyncProperties.getUpdate().getDelayMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("업데이트 동기화 대기 중 인터럽트가 발생했습니다.", e);
        }
    }

    private AnimalSyncResponse createResponse(String message, int savedCount, Instant startedAt) {
        return new AnimalSyncResponse(message, savedCount, elapsedMs(startedAt));
    }

    private long elapsedMs(Instant startedAt) {
        return Duration.between(startedAt, Instant.now()).toMillis();
    }
}
