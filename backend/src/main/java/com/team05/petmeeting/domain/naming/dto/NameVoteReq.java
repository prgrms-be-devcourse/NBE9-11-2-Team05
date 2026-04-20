package com.team05.petmeeting.domain.naming.dto;
// 투표 DTO
public record NameVoteReq(
    Long candidateId,
    int currentVoteCount
) {
}
