package com.team05.petmeeting.domain.adoption.dto.request;

import com.team05.petmeeting.domain.adoption.entity.AdoptionStatus;
import lombok.Getter;

@Getter
public class AdoptionReviewRequest {
    // 보호소 관리자가 변경하려는 입양 신청 상태
    private AdoptionStatus status;

    // 입양 신청 거절 시 관리자가 작성하는 사유
    private String rejectionReason;
}
