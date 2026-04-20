package com.team05.petmeeting.domain.adoption.dto.response;

import com.team05.petmeeting.domain.adoption.entity.AdoptionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdoptionDetailResponse {
    private Long applicationId;
    private AdoptionStatus status;
    private String applyReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    private String applyTel;
    private AnimalInfo animalInfo;

    @Getter
    public static class AnimalInfo{
        private String desertionNo;
        private String username;
        private String specialMark; // 사진 URL
        private String careNm; // 보호소 이름
        private String careOwerNm; // 보호소 담당자
        private String careTel; // 보호소 전화번호
        private String careAddr; // 보호소 주소
    }
}
