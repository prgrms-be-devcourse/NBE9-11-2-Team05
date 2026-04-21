package com.team05.petmeeting.domain.ads.controller;

import com.team05.petmeeting.domain.ads.service.AdsService;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ads")
public class AdsController {
    //수동 트리거용 API 엔드포인트
    private final AdsService adsService;

    // Top N 동물 조회
    @GetMapping("/top-animals")
    public RsData<List<Animal>> getTopAnimals(
            @RequestParam(defaultValue = "3") int n
    ) {
        List<Animal> topAnimals = adsService.getTopAnimals(n);
        return new RsData<>("Top N 동물 조회 성공", "200", topAnimals);
    }
}