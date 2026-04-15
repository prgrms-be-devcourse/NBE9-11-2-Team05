package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.dto.AnimalRes;
import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.errorCode.AnimalErrorCode;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public Animal findByAnimalId(Long animalId){
        return animalRepository.findById(animalId).orElseThrow(() -> new BusinessException(AnimalErrorCode.ANIMAL_NOT_FOUND));
    }

    public Page<AnimalRes> getAnimals(String region, String kind, String processState, Pageable pageable) {
        // 예외처리 (페이지 번호 음수)
        if (pageable.getPageNumber() < 0) {
            throw new BusinessException(AnimalErrorCode.INVALID_PAGE_NUMBER);
        }

        List<Animal> sortedAnimals = animalRepository.findAll(pageable.getSort()); // 정렬된 동물 리스트

        // 필터링 로직
        List<AnimalRes> filterdList = sortedAnimals.stream()
                .filter(animal -> filterByRegion(animal, region))
                .filter(animal -> filterByKind(animal, kind))
                .filter(animal -> filterByProcessState(animal, processState))
                .map(AnimalRes::new)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filterdList.size());

        // 데이터가 아예 없을 때
        if (filterdList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        // 페이지 범위를 벗어났을 때
        if (start >= filterdList.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, filterdList.size());
        }

        return new PageImpl<>(
                filterdList.subList(start, end), // 현재 페이지 조각
                pageable,                        // 요청 정보
                filterdList.size()               // 전체 개수
        );

    }

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
