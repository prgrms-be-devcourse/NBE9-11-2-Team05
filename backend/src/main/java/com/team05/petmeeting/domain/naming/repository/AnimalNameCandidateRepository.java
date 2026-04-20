package com.team05.petmeeting.domain.naming.repository;

import com.team05.petmeeting.domain.naming.entity.AnimalNameCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalNameCandidateRepository extends JpaRepository<AnimalNameCandidate, Long> {
}
