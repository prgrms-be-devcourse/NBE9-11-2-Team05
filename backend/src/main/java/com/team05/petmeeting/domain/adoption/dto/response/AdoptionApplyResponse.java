package com.team05.petmeeting.domain.adoption.dto.response;

import com.team05.petmeeting.domain.adoption.entity.AdoptionStatus;
import lombok.Getter;

@Getter
public class AdoptionApplyResponse {
    private Long applicationId;
    private AdoptionStatus status;
    private AnimalInfo animalInfo;

    @Getter
    public static class AnimalInfo{
        private String desertionNo;
        private String username;
        private String kindFullNm;
        private String careNm; // 보호소 이름
    }

}
