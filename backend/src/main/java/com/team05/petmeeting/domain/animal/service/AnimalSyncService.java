package com.team05.petmeeting.domain.animal.service;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimalSyncService {
    private static final LocalDate INITIAL_SYNC_START_DATE = LocalDate.of(2008, 1, 1);
    private static final long UPDATE_SYNC_DELAY_MS = 300L;
    private static final String SYNC_PAGE_MESSAGE = "유기동물 데이터 동기화 완료";
    private static final String INITIAL_SYNC_MESSAGE = "INITIAL_MONTHLY_SYNC_OK";
    private static final String UPDATE_SYNC_MESSAGE = "UPDATE_SYNC_OK";

    private final AnimalExternalService animalExternalService;
    private final AnimalRepository animalRepository;
    private final SyncStateRepository syncStateRepository;
    private final ShelterService shelterService;

    private record SyncPageResult(
            String message,
            int savedCount,
            long elapsedMs
    ) {
    }

    // 특정 페이지를 한 번 조회해 동물 데이터를 저장한다.
    @Transactional
    public AnimalSyncResponse fetchAndSaveAnimals(int pageNo, int numOfRows) {
        SyncPageResult result = fetchAndInsertAnimals(pageNo, numOfRows, null, null, Integer.MAX_VALUE);
        log.info(
                "Animal sync completed: pageNo={}, numOfRows={}, savedCount={}, elapsedMs={}",
                pageNo,
                numOfRows,
                result.savedCount(),
                result.elapsedMs()
        );
        return new AnimalSyncResponse(result.message(), result.savedCount(), result.elapsedMs());
    }

    // 2008년 1월부터 현재까지 월 단위로 나눠 최초 적재를 수행한다.
    @Transactional
    public AnimalSyncResponse runInitialMonthlySync(int numOfRows) {
        Instant startedAt = Instant.now();
        int savedCount = fetchAndSaveMonthlyAnimalsFrom2008(numOfRows, Integer.MAX_VALUE);
        updateSyncState(AnimalSyncType.INITIAL);

        long elapsedMs = elapsedMs(startedAt);
        log.info(
                "Initial animal sync completed: from={}, to={}, numOfRows={}, savedCount={}, elapsedMs={}",
                INITIAL_SYNC_START_DATE,
                LocalDate.now(),
                numOfRows,
                savedCount,
                elapsedMs
        );
        return new AnimalSyncResponse(INITIAL_SYNC_MESSAGE, savedCount, elapsedMs);
    }

    // 마지막 업데이트 시점 이후 수정된 데이터를 다시 반영한다.
    @Transactional
    public AnimalSyncResponse runUpdateSync(int numOfRows) {
        Instant startedAt = Instant.now();
        LocalDate bgupd = getUpdateStartDate();
        LocalDate enupd = LocalDate.now();
        int savedCount = fetchAndSaveAnimalsByUpdatedDate(bgupd, enupd, numOfRows);
        updateSyncState(AnimalSyncType.UPDATE);

        long elapsedMs = elapsedMs(startedAt);
        log.info(
                "Animal update sync completed: from={}, to={}, numOfRows={}, savedCount={}, elapsedMs={}",
                bgupd,
                enupd,
                numOfRows,
                savedCount,
                elapsedMs
        );
        return new AnimalSyncResponse(UPDATE_SYNC_MESSAGE, savedCount, elapsedMs);
    }

    // 최초 적재 범위를 월별로 순회하면서 저장 건수를 누적한다.
    @Transactional
    public int fetchAndSaveMonthlyAnimalsFrom2008(int numOfRows, int maxSaveCount) {
        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart = INITIAL_SYNC_START_DATE.withDayOfMonth(1);
        int totalSavedCount = 0;

        while (!currentMonthStart.isAfter(today) && totalSavedCount < maxSaveCount) {
            LocalDate currentMonthEnd = currentMonthStart.withDayOfMonth(currentMonthStart.lengthOfMonth());
            if (currentMonthEnd.isAfter(today)) {
                currentMonthEnd = today;
            }

            totalSavedCount += fetchAndSaveAnimalsByDateRange(
                    currentMonthStart,
                    currentMonthEnd,
                    numOfRows,
                    maxSaveCount - totalSavedCount
            );
            currentMonthStart = currentMonthStart.plusMonths(1).withDayOfMonth(1);
        }

        return totalSavedCount;
    }

    // 수정일 기준 조회 결과를 페이지 단위로 끝까지 저장하거나 갱신한다.
    @Transactional
    public int fetchAndSaveAnimalsByUpdatedDate(LocalDate bgupd, LocalDate enupd, int numOfRows) {
        int pageNo = 1;
        int totalSavedCount = 0;

        while (true) {
            AnimalApiResponse response = animalExternalService.fetchAnimalsByUpdatedDate(pageNo, numOfRows, bgupd, enupd);
            List<AnimalItem> items = extractItems(response);

            if (items.isEmpty()) {
                break;
            }

            totalSavedCount += saveOrUpdateAnimals(items);
            pageNo++;
            waitForNextUpdatePage();
        }

        return totalSavedCount;
    }

    // 일반 조회 API를 한 번 호출해 현재 페이지 저장 결과를 만든다.
    private SyncPageResult fetchAndInsertAnimals(
            int pageNo,
            int numOfRows,
            LocalDate bgnde,
            LocalDate endde,
            int maxSaveCount
    ) {
        Instant startedAt = Instant.now();
        AnimalApiResponse response = animalExternalService.fetchAnimals(pageNo, numOfRows, bgnde, endde);
        List<AnimalItem> items = extractItems(response);

        if (items.isEmpty()) {
            return new SyncPageResult(SYNC_PAGE_MESSAGE, 0, elapsedMs(startedAt));
        }

        int savedCount = saveNewAnimals(items, maxSaveCount);
        return new SyncPageResult(SYNC_PAGE_MESSAGE, savedCount, elapsedMs(startedAt));
    }

    // 한 달 범위를 페이지별로 조회하면서 저장 가능한 동물을 누적한다.
    private int fetchAndSaveAnimalsByDateRange(LocalDate bgnde, LocalDate endde, int numOfRows, int maxSaveCount) {
        int pageNo = 1;
        int totalSavedCount = 0;

        while (totalSavedCount < maxSaveCount) {
            SyncPageResult result = fetchAndInsertAnimals(pageNo, numOfRows, bgnde, endde, maxSaveCount - totalSavedCount);
            if (result.savedCount() == 0) {
                break;
            }

            totalSavedCount += result.savedCount();
            pageNo++;
        }

        return totalSavedCount;
    }

    // 최초 적재에서는 보호소를 먼저 저장한 뒤, 현재 페이지의 신규 동물만 저장한다.
    private int saveNewAnimals(List<AnimalItem> items, int maxSaveCount) {
        syncShelters(items);

        List<Animal> animalsToSave = new ArrayList<>();
        Set<String> seenDesertionNos = new HashSet<>();

        for (AnimalItem item : items) {
            if (animalsToSave.size() >= maxSaveCount) {
                break;
            }

            if (item.getDesertionNo() == null || item.getDesertionNo().isBlank()) {
                continue;
            }

            if (!seenDesertionNos.add(item.getDesertionNo())) {
                continue;
            }

            animalsToSave.add(Animal.from(item));
        }

        animalRepository.saveAll(animalsToSave);
        return animalsToSave.size();
    }

    // 업데이트 적재에서는 보호소를 먼저 저장한 뒤, 기존 동물은 수정하고 없으면 새로 저장한다.
    private int saveOrUpdateAnimals(List<AnimalItem> items) {
        syncShelters(items);

        int savedCount = 0;

        for (AnimalItem item : items) {
            if (item.getDesertionNo() == null || item.getDesertionNo().isBlank()) {
                continue;
            }

            animalRepository.findByDesertionNo(item.getDesertionNo())
                    .ifPresentOrElse(
                            animal -> animal.updateFrom(item),
                            () -> animalRepository.save(Animal.from(item))
                    );
            savedCount++;
        }

        return savedCount;
    }

    // 동물 저장 전에 보호소 정보를 먼저 upsert 한다.
    private void syncShelters(List<AnimalItem> items) {
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
                .distinct()
                .toList();

        shelterService.createOrUpdateShelters(shelterCmds);
    }

    // 외부 API 응답에서 실제 동물 목록만 꺼내고, 비정상 구조면 빈 리스트를 돌려준다.
    private List<AnimalItem> extractItems(AnimalApiResponse response) {
        if (response == null || response.getResponse() == null) {
            return List.of();
        }

        var body = response.getResponse().getBody();
        if (body == null || body.getItems() == null || body.getItems().getItem() == null) {
            return List.of();
        }

        return body.getItems().getItem();
    }

    // 마지막 UPDATE 동기화 시각을 기준으로 다음 업데이트 시작 날짜를 정한다.
    private LocalDate getUpdateStartDate() {
        LocalDateTime lastUpdatedAt = syncStateRepository.findBySyncType(AnimalSyncType.UPDATE)
                .map(SyncState::getLastUpdatedAt)
                .orElse(null);

        if (lastUpdatedAt == null) {
            return INITIAL_SYNC_START_DATE;
        }

        return lastUpdatedAt.toLocalDate();
    }

    // 동기화가 끝난 시각을 sync type 별로 저장한다.
    private void updateSyncState(AnimalSyncType syncType) {
        SyncState syncState = syncStateRepository.findBySyncType(syncType)
                .orElseGet(() -> SyncState.create(syncType));

        syncState.updateLastUpdatedAt(LocalDateTime.now());
        syncStateRepository.save(syncState);
    }

    // 업데이트 API를 연속 호출할 때 외부 서버 부담을 줄이기 위해 잠시 대기한다.
    private void waitForNextUpdatePage() {
        try {
            Thread.sleep(UPDATE_SYNC_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("업데이트 동기화 대기 중 인터럽트가 발생했습니다.", e);
        }
    }

    // 시작 시각부터 현재까지 걸린 시간을 밀리초로 계산한다.
    private long elapsedMs(Instant startedAt) {
        return Duration.between(startedAt, Instant.now()).toMillis();
    }
}
