package com.team05.demo.domain.cheer.repository;

import com.team05.demo.domain.cheer.entity.Cheer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheerRepository extends JpaRepository<Cheer, Long> {
    List<Cheer> findByUserId(Long userId);
}