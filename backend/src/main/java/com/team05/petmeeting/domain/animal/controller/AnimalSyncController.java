package com.team05.petmeeting.domain.animal.controller;

import com.team05.petmeeting.domain.animal.service.AnimalSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
public class AnimalSyncController {

    private final AnimalSyncService animalSyncService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncAnimals(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows
    ) {
        animalSyncService.fetchAndSaveAnimals(pageNo, numOfRows);
        return ResponseEntity.ok("유기동물 데이터 동기화 완료");
    } // 특정 페이지의 데이터를 동기화하는 엔드포인트

    @PostMapping("/sync/all")
    public ResponseEntity<String> syncAllAnimals(
            @RequestParam(defaultValue = "10") int numOfRows
    ) {
        System.out.println("sync/all controller entered");
        animalSyncService.fetchAndSaveAllAnimals(numOfRows);
        return ResponseEntity.ok("SYNC_ALL_OK");
    } // 전체 데이터를 페이지 단위로 반복해서 동기화하는 엔드포인트
}
