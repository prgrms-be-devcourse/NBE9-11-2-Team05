package com.team05.demo.domain.cheer.repository;

import com.team05.demo.domain.cheer.entity.Cheer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheerRepository extends JpaRepository<Cheer, Long> {
    int countByAnimalId(Long animalId); // 쿼리 최적화
}