package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.dto.external.AnimalApiResponse;
import com.team05.demo.domain.animal.dto.external.AnimalItem;
import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// 유기동물 데이터를 외부 API에서 조회하여 DB에 저장하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class AnimalSyncService {
    private final AnimalExternalService animalExternalService;
    private final AnimalRepository animalRepository;

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

