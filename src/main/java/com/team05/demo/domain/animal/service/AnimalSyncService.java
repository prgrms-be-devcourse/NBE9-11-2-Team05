package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.dto.external.AnimalApiResponse;
import com.team05.demo.domain.animal.dto.external.AnimalItem;
import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 유기동물 데이터를 외부 API에서 조회하여 DB에 저장하는 서비스 클래스
@Service
@Transactional
public class AnimalSyncService {
    private final AnimalExternalService animalExternalService;
    private final AnimalRepository animalRepository;

    public AnimalSyncService(AnimalExternalService animalExternalService, AnimalRepository animalRepository) {
        this.animalExternalService = animalExternalService;
        this.animalRepository = animalRepository;
    }

    public void fetchAndSaveAnimals() {
        int pageSize = 500;

        AnimalApiResponse firstResponse = animalExternalService.fetchAnimals(1, pageSize);

        int totalCount = firstResponse.getResponse().getBody().getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        saveItems(firstResponse);

        for (int page = 2; page <= totalPages; page++) {
            AnimalApiResponse response = animalExternalService.fetchAnimals(page, pageSize);
            saveItems(response);
        }

    }

    private void saveItems(AnimalApiResponse response) {
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
