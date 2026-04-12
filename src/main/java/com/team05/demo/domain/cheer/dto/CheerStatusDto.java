package com.team05.demo.domain.cheer.dto;

public record CheerStatusDto(
        long usedToday,
        int remainingToday,
        String resetAt
){

}
