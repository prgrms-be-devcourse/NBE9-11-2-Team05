package com.team05.demo.domain.cheer.dto;

public record AnimalCheersDto(
        Long animalId,
        int cheerCount,     // 총 응원 수
        double temperature  // 온도
) {
}
