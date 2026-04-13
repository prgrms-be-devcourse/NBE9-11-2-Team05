package com.team05.demo.domain.animal.controller;

import com.team05.demo.domain.animal.service.AnimalSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
public class AnimalSyncController {

    private final AnimalSyncService animalSyncService;

    @PostMapping("/sync")
    public String syncAnimals() {
        animalSyncService.fetchAndSaveAnimals();
        return "유기동물 데이터 저장 완료";
    }
}
