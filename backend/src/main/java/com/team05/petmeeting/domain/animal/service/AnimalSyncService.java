package com.team05.petmeeting.domain.animal.service;

import com.team05.petmeeting.domain.animal.dto.external.AnimalApiResponse;
import com.team05.petmeeting.domain.animal.dto.external.AnimalItem;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.entity.AnimalSyncType;
import com.team05.petmeeting.domain.animal.entity.SyncState;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import com.team05.petmeeting.domain.animal.repository.SyncStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 유기동물 데이터를 외부 API에서 조회하여 DB에 저장하는 서비스 클래스
@Service
@Slf4j
@RequiredArgsConstructor
public class AnimalSyncService {
    private static final LocalDate INITIAL_SYNC_START_DATE = LocalDate.of(2008, 1, 1);
    private static final long UPDATE_SYNC_DELAY_MS = 300L;

    public record SyncResult(
            String message,
            int savedCount,
            long elapsedMs
    ) {
    }

    private final AnimalExternalService animalExternalService;
    private final AnimalRepository animalRepository;
    private final SyncStateRepository syncStateRepository;
    private final Clock clock;

    // 특정 페이지 번호와 페이지당 항목 수를 기준으로 유기동물 데이터를 조회하고 DB에 저장하는 메서드
    @Transactional
    public SyncResult fetchAndSaveAnimals(int pageNo, int numOfRows) {
        return fetchAndInsertAnimals(pageNo, numOfRows, null, null, Integer.MAX_VALUE);
    }

    @Transactional
    public SyncResult fetchAndInsertAnimals(int pageNo, int numOfRows, LocalDate bgnde, LocalDate endde) {
        return fetchAndInsertAnimals(pageNo, numOfRows, bgnde, endde, Integer.MAX_VALUE);
    }

    @Transactional
    public SyncResult fetchAndInsertAnimals(
            int pageNo,
            int numOfRows,
            LocalDate bgnde,
            LocalDate endde,
            int maxSaveCount
    ) {
        Instant startedAt = clock.instant();
        AnimalApiResponse response = animalExternalService.fetchAnimals(pageNo, numOfRows, bgnde, endde);

        List<AnimalItem> items = response.getResponse()
                .getBody()
                .getItems()
                .getItem();

        if (items == null || items.isEmpty()) {
            long elapsedMs = Duration.between(startedAt, clock.instant()).toMillis();
            log.info("Animal sync page completed: pageNo={}, numOfRows={}, bgnde={}, endde={}, savedCount=0, elapsedMs={}",
                    pageNo, numOfRows, bgnde, endde, elapsedMs);
            return new SyncResult("유기동물 데이터 동기화 완료", 0, elapsedMs);
        }

        int savedCount = saveNewAnimals(items, maxSaveCount);
        long elapsedMs = Duration.between(startedAt, clock.instant()).toMillis();
        log.info("Animal sync page completed: pageNo={}, numOfRows={}, bgnde={}, endde={}, savedCount={}, elapsedMs={}",
                pageNo, numOfRows, bgnde, endde, savedCount, elapsedMs);
        return new SyncResult("유기동물 데이터 동기화 완료", savedCount, elapsedMs);
    }

    public SyncResult runInitialMonthlySync(int numOfRows) {
        Instant startedAt = clock.instant();
        int savedCount = fetchAndSaveMonthlyAnimalsFrom2008(numOfRows);
        updateSyncState(AnimalSyncType.INITIAL);
        return new SyncResult("INITIAL_MONTHLY_SYNC_OK", savedCount, Duration.between(startedAt, clock.instant()).toMillis());
    }

    @Transactional
    public int fetchAndSaveMonthlyAnimalsFrom2008(int numOfRows) {
        return fetchAndSaveMonthlyAnimalsFrom2008(numOfRows, Integer.MAX_VALUE);
    }

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

    public SyncResult runUpdateSync(int numOfRows) {
        Instant startedAt = clock.instant();
        LocalDate bgupd = getUpdateStartDate();
        LocalDate enupd = LocalDate.now();
        fetchAndSaveAnimalsByUpdatedDate(bgupd, enupd, numOfRows);
        updateSyncState(AnimalSyncType.UPDATE);
        return new SyncResult("UPDATE_SYNC_OK", 0, Duration.between(startedAt, clock.instant()).toMillis());
    }

    @Transactional
    public void fetchAndSaveAnimalsByUpdatedDate(LocalDate bgupd, LocalDate enupd, int numOfRows) {
        int pageNo = 1;

        while (true) {
            Instant startedAt = clock.instant();
            AnimalApiResponse response = animalExternalService.fetchAnimalsByUpdatedDate(pageNo, numOfRows, bgupd, enupd);
            List<AnimalItem> items = response.getResponse()
                    .getBody()
                    .getItems()
                    .getItem();

            if (items == null || items.isEmpty()) {
                log.info("Animal update sync page completed: pageNo={}, numOfRows={}, bgupd={}, enupd={}, savedCount=0, elapsedMs={}",
                        pageNo, numOfRows, bgupd, enupd, Duration.between(startedAt, clock.instant()).toMillis());
                break;
            }

            saveOrUpdateAnimals(items);
            log.info("Animal update sync page completed: pageNo={}, numOfRows={}, bgupd={}, enupd={}, savedCount={}, elapsedMs={}",
                    pageNo, numOfRows, bgupd, enupd, items.size(), Duration.between(startedAt, clock.instant()).toMillis());
            pageNo++;

            try {
                Thread.sleep(UPDATE_SYNC_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("업데이트 동기화 대기 중 인터럽트가 발생했습니다.", e);
            }
        }
    }

    private LocalDate getUpdateStartDate() {
        LocalDateTime lastUpdatedAt = syncStateRepository.findBySyncType(AnimalSyncType.UPDATE)
                .map(SyncState::getLastUpdatedAt)
                .orElse(null);

        if (lastUpdatedAt == null) {
            return INITIAL_SYNC_START_DATE;
        }

        return lastUpdatedAt.toLocalDate();
    }

    private void updateSyncState(AnimalSyncType syncType) {
        SyncState syncState = syncStateRepository.findBySyncType(syncType)
                .orElseGet(() -> SyncState.create(syncType));

        syncState.updateLastUpdatedAt(LocalDateTime.now());
        syncStateRepository.save(syncState);
    }

    private int fetchAndSaveAnimalsByDateRange(LocalDate bgnde, LocalDate endde, int numOfRows, int maxSaveCount) {
        int pageNo = 1;
        int totalSavedCount = 0;

        while (totalSavedCount < maxSaveCount) {
            SyncResult result = fetchAndInsertAnimals(pageNo, numOfRows, bgnde, endde, maxSaveCount - totalSavedCount);

            if (result.savedCount() == 0) {
                break;
            }

            totalSavedCount += result.savedCount();
            pageNo++;
        }

        return totalSavedCount;
    }

    private int saveNewAnimals(List<AnimalItem> items, int maxSaveCount) {
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

    private void saveOrUpdateAnimals(List<AnimalItem> items) {
        for (AnimalItem item : items) {
            if (item.getDesertionNo() == null || item.getDesertionNo().isBlank()) {
                continue;
            }

            animalRepository.findByDesertionNo(item.getDesertionNo())
                    .ifPresentOrElse(
                            animal -> animal.updateFrom(item),
                            () -> animalRepository.save(Animal.from(item))
                    );
        }
    }
}
