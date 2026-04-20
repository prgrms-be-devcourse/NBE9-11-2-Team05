package com.team05.petmeeting.domain.animal.repository;

import com.team05.petmeeting.domain.animal.entity.Animal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
// JpaRepository 기본 기능 + Custom 기능 모두 보유
public interface AnimalRepository extends JpaRepository<Animal, Long>, AnimalRepositoryCustom {
    // 유기번호로 조회
    Optional<Animal> findByDesertionNo(String desertionNo);

    boolean existsByDesertionNo(String desertionNo);

    // 원자적 UPDATE
    @Modifying(clearAutomatically = true) // 영속성 컨텍스트를 비워주는 설정 -> 쿼리 사용후 캐시삭제
    @Query("UPDATE Animal a SET a.totalCheerCount = a.totalCheerCount + 1 " +
            "WHERE a.id = :animalId")
    void incrementCheerCount(@Param("animalId") Long animalId);

    // 응원 수 기준 상위 N개 동물 조회
    List<Animal> findAllByOrderByTotalCheerCountDesc(Pageable pageable);
}
