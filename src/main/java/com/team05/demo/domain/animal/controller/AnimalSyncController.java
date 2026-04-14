package com.team05.demo.domain.animal.controller;

import com.team05.demo.domain.animal.service.AnimalSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
public class AnimalSyncController {

    private final AnimalSyncService animalSyncService;

    // 유기동물 데이터를 외부 API에서 조회하여 DB에 저장하는 엔드포인트
    @PostMapping("/sync")
    public ResponseEntity<String> syncAnimals() {
        animalSyncService.fetchAndSaveAnimals();
        return ResponseEntity.ok("동기화 완료");
    }
}
