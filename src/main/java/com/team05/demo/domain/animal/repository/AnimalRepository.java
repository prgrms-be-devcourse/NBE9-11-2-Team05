package com.team05.demo.domain.animal.repository;

import com.team05.demo.domain.animal.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
