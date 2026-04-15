package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.errorCode.AnimalErrorCode;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public Animal findByAnimalId(Long animalId){
        return animalRepository.findById(animalId).orElseThrow(() -> new BusinessException(AnimalErrorCode.ANIMAL_NOT_FOUND));
    }
/**
    public Page<AnimalResponse> getAnimals(String region, String kind, String processState) {
        List<Animal> allAnimals = animalRepository.findAll();

        // 필터링 로직
        allAnimals.stream()
                .filter(animal -> filterByRegion(animal, region))
                .filter(animal -> filterByKind(animal, kind))
                .filter(animal -> filterByProcessState(animal, processState))
                // 이후 entity->DTO 변환,

        // todo: 페이징처리

    }
*/




    // [지역필터] 강원-원주-2026-00156 에선 "강원"으로 필터링
    private boolean filterByRegion(Animal animal, String region) {
        if(region == null || region.isEmpty()) return true;
        String area = animal.getNoticeNo(); // 강원-원주-2026-00156

        return area.contains(region);
    }

    // [축종 필터] 개, 고양이, 기타
    private boolean filterByKind(Animal animal, String kind) {
        if(kind == null || kind.isEmpty()) return true;
        String upKindNm = animal.getUpKindNm(); // 개, 고양이, 기타

        if (kind.equals("기타")) {
            return !upKindNm.equals("개") && !upKindNm.equals("고양이");
        }

        return upKindNm.contains(kind); // 기타(앵무새) 같은 형식으로 기억해서 contains 사용했습니다.
    }


    // [상태 필터] "보호중", "종료" 포함 여부
    private boolean filterByProcessState(Animal animal, String processState) {
        if(processState == null || processState.isEmpty()) return true;
        return animal.getProcessState().contains(processState);
    }



}
