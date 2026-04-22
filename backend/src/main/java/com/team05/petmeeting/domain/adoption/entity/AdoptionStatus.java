package com.team05.petmeeting.domain.adoption.entity;

public enum AdoptionStatus {
    // 신청 접수 후 보호소 관리자가 아직 심사를 시작하지 않은 상태
    PendingReview,

    // 보호소 관리자가 입양 신청을 심사 중인 상태
    Reviewing,

    // 입양 신청 심사가 승인 완료된 상태
    Approved,

    // 입양 신청 심사가 거절 완료된 상태
    Rejected
}
