package com.team05.demo.domain.cheer.controller;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.domain.cheer.service.CheerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CheerController", description = "응원 API")
@RequiredArgsConstructor
public class CheerController {

    private final AnimalRepository animalRepository; // todo: AnimalService 정의 후 교체 예정(예외처리도 서비스에서 진행될 예정)
    private final CheerService cheerService;


    @GetMapping("/cheers/today")
    @Operation(summary = "잔여 응원 횟수 조회")
    public void getTodaysCheers() {

    }


    @GetMapping("/animals/{animalId}/cheers")
    @Operation(summary = "응원 수 및 온도 조회")
    public void getAnimalCheers(@PathVariable long animalId) {
        Animal animal = animalRepository.findById(animalId).get();
        // 예외처리 Animal서비스에서 처리
        Integer totalCheerCount = animal.getTotalCheerCount();


    }


    @PostMapping("/animals/{animalId}/cheers")
    @Operation(summary = "응원 부여")
    public void cheerAnimal() {

    }




}
