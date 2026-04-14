package com.team05.demo.domain.cheer.dto;

public record CheerStatusDto(
        long usedToday,
        int remainingToday,
        String resetAt      // "2024-08-10T00:00:00"
){

}
