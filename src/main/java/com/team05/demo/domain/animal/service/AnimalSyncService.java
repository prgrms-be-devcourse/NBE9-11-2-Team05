package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.dto.external.AnimalApiResponse;
import com.team05.demo.domain.animal.dto.external.AnimalItem;
import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 유기동물 데이터를 외부 API에서 조회하여 DB에 저장하는 서비스 클래스
@Service
public class AnimalSyncService {
    private static final Logger log = LoggerFactory.getLogger(AnimalSyncService.class);
    private static final int EXTERNAL_PAGE_SIZE = 500;
    private static final int DB_BATCH_SIZE = 100;

    private final AnimalExternalService animalExternalService;
    private final AnimalRepository animalRepository;
    private final Counter syncRunCounter;
    private final DistributionSummary syncFetchedCountSummary;
    private final DistributionSummary syncSavedCountSummary;
    private final DistributionSummary syncSkippedCountSummary;

    @Autowired
    public AnimalSyncService( AnimalExternalService animalExternalService,
                              AnimalRepository animalRepository,
                              MeterRegistry meterRegistry) {

        this.animalExternalService = animalExternalService;
        this.animalRepository = animalRepository;

        this.syncRunCounter = Counter.builder("animal.sync.runs")
                .description("Number of animal sync executions")
                .register(meterRegistry);

        this.syncFetchedCountSummary = DistributionSummary.builder("animal.sync.fetched.count")
                .description("Number of animal records fetched in each sync")
                .baseUnit("items")
                .register(meterRegistry);

        this.syncSavedCountSummary = DistributionSummary.builder("animal.sync.saved.count")
                .description("Number of new animal records saved in each sync")
                .baseUnit("items")
                .register(meterRegistry);

        this.syncSkippedCountSummary = DistributionSummary.builder("animal.sync.skipped.count")
                .description("Number of duplicate animal records skipped in each sync")
                .baseUnit("items")
                .register(meterRegistry);
    }

    public void fetchAndSaveAnimals() {
        AnimalApiResponse response = animalExternalService.fetchAnimals();

        List<AnimalItem> items = response.getResponse()
                .getBody()
                .getItems()
                .getItem();

        List<Animal> animals = items.stream()
                .filter(item -> !animalRepository.existsByDesertionNo(item.getDesertionNo()))
                .map(Animal::from)
                .toList();

        animalRepository.saveAll(animals);
    }

}

