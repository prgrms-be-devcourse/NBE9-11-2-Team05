package com.team05.demo.domain.cheer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CheerController", description = "응원 API")
public class CheerController {


    @GetMapping("/cheers/today")
    @Operation(summary = "잔여 응원 횟수 조회")
    public void getTodaysCheers() {

    }


    @GetMapping("/animals/{animalId}/cheers")
    @Operation(summary = "응원 수 및 온도 조회")
    public void getAnimalCheers() {

    }


    @PostMapping("/animals/{animalId}/cheers")
    @Operation(summary = "응원 부여")
    public void cheerAnimal() {

    }




}
