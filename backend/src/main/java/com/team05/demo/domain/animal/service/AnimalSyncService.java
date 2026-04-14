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

    public int fetchAndSaveAnimals(int pageNo, int numOfRows) {
        AnimalApiResponse response = animalExternalService.fetchAnimals(pageNo, numOfRows);

        List<AnimalItem> items = response.getResponse()
                .getBody()
                .getItems()
                .getItem();

        if (items == null || items.isEmpty()) {
            return 0;
        } // API에서 받은 데이터 중에서 desertionNo가 존재하고, DB에 해당 desertionNo가 없는 경우에만 저장한다.


        List<Animal> animals = items.stream()
                .filter(item -> item.getDesertionNo() != null && !item.getDesertionNo().isBlank())
                .filter(item -> !animalRepository.existsByDesertionNo(item.getDesertionNo()))
                .map(Animal::from)
                .toList();

        animalRepository.saveAll(animals);

        return items.size(); // 저장된 데이터 수를 반환한다.
    }

    // 전체 데이터를 페이지 단위로 반복해서 조회하고 저장하는 메서드
    public void fetchAndSaveAllAnimals(int numOfRows) {
        int pageNo = 1;

        while (true) {
            int fetchedCount = fetchAndSaveAnimals(pageNo, numOfRows);

            if (fetchedCount == 0) {
                break;
            } //저장할 데이터가 없으면 루프를 종료

            pageNo++; // 다음 페이지로 이동한다.
        }
    }

}

