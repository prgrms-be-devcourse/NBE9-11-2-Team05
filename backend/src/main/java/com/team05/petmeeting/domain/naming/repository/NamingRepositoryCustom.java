package com.team05.petmeeting.domain.naming.repository;

import com.team05.petmeeting.domain.naming.dto.NameCandidateRes;

public interface NamingRepositoryCustom {

    NameCandidateRes getCandidates(
            Long animalId,
            Long userId
    );
}
