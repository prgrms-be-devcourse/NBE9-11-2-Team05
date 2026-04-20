package com.team05.petmeeting.domain.adoption.repository;

import com.team05.petmeeting.domain.adoption.entity.AdoptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
    List<AdoptionApplication> findByUser_Id(Long user_Id);
    Optional<AdoptionApplication> findByIdAndUser_Id(Long applicationId, Long userId);
}
