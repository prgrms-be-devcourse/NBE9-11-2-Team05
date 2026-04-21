package com.team05.petmeeting.domain.adoption.dto.request;

import com.team05.petmeeting.domain.adoption.entity.AdoptionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class AdoptionReviewRequest {
    @Enumerated(EnumType.STRING)
    private AdoptionStatus status;
    private String rejectionReason;
}