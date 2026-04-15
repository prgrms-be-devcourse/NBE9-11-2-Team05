package com.team05.petmeeting.domain.animal.repository;

import com.team05.petmeeting.domain.animal.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    // 유기번호로 조회
    Optional<Animal> findByDesertionNo(String desertionNo);

    boolean existsByDesertionNo(String desertionNo);

    // 원자적 UPDATE
    @Modifying
    @Query("UPDATE Animal a SET a.totalCheerCount = a.totalCheerCount + 1 " +
            "WHERE a.id = :animalId")
    void incrementCheerCount(@Param("animalId") Long animalId);
}
