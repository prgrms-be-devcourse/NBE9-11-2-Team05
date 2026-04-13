package com.team05.demo.domain.animal.repository;

import com.team05.demo.domain.animal.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findByDesertionNo(String desertionNo);
    boolean existsByDesertionNo(String desertionNo);
}
