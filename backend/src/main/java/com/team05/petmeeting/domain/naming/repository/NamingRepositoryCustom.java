package com.team05.petmeeting.domain.naming.repository;

import com.team05.petmeeting.domain.naming.dto.NameCandidateRes;

import java.util.Optional;

public interface NamingRepositoryCustom {

    NameCandidateRes getCandidates(
            Long animalId,
            Long userId
    );

    Optional<NameCandidateRes.CandidateDto> getTopQualifiedCandidate(
            Long animalId,
            String careRegNo,
            int threshold
    );
}
