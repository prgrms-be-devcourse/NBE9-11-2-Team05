package com.team05.petmeeting.domain.animal.service;

import com.team05.petmeeting.domain.animal.dto.external.AnimalApiResponse;
import com.team05.petmeeting.domain.animal.dto.external.AnimalItem;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import com.team05.petmeeting.domain.shelter.dto.ShelterCommand;
import com.team05.petmeeting.domain.shelter.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 유기동물 데이터를 외부 API에서 조회하여 DB에 저장하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class AnimalSyncService {
    private final AnimalExternalService animalExternalService;
    private final AnimalRepository animalRepository;
    private final ShelterService shelterService;

    // 특정 페이지 번호와 페이지당 항목 수를 기준으로 유기동물 데이터를 조회하고 DB에 저장하는 메서드
    @Transactional
    public int fetchAndSaveAnimals(int pageNo, int numOfRows) {
        AnimalApiResponse response = animalExternalService.fetchAnimals(pageNo, numOfRows);

        List<AnimalItem> items = response.getResponse()
                .getBody()
                .getItems()
                .getItem();

        if (items == null || items.isEmpty()) {
            return 0;
        }

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

        // 조회된 각 유기동물 데이터를 DB에 저장하거나 업데이트하는 로직
        for (AnimalItem item : items) {
            if (item.getDesertionNo() == null || item.getDesertionNo().isBlank()) {
                continue;
            }

            animalRepository.findByDesertionNo(item.getDesertionNo())
                    .ifPresentOrElse(
                            animal -> {
                                if (animal.needsProcessStateUpdate(item)) {
                                    animal.updateProcessState(item);
                                }
                            },
                            () -> animalRepository.save(Animal.from(item))
                    );
        }

        return items.size();
    }

    // 전체 데이터를 페이지 단위로 반복해서 조회하고 저장하는 메서드
    public void fetchAndSaveAllAnimals(int numOfRows) {
        int pageNo = 1;

        while (true) {
            int fetchedCount = fetchAndSaveAnimals(pageNo, numOfRows);

            if (fetchedCount == 0) {
                break;
            }

            pageNo++;
        }
    }
}

