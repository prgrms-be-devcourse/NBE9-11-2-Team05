package com.team05.demo.domain.animal.controller;

import com.team05.demo.domain.animal.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;
/**
    // todo: 다중정렬 (공고 종료일 같은경우, 다음 정렬 조건), getAnimals(): 필터조건 null체크 & Dto 반환
    @GetMapping()
    @Operation(summary = "유기동물 필터 적용 조회", description = "필터(지역, 종류)와 페이징/정렬을 지원합니다.")
    public ResponseEntity<Page<AnimalResponse>> animalList(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String processState,
            @PageableDefault(page = 10, sort = "noticeEdt", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<AnimalResponse> response = animalService.getAnimals(region, kind, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{animalId}")
    @Operation(summary = "유기동물 상세 조회")
    public ResponseEntity<AnimalDetailRes> animalDetail(
            @PathVariable Long animalId
    ) {
        AnimalDetailRes response = animalService.getAnimalDetail(animalId);
        return ResponseEntity.ok(response);
    }

*/
}
