package com.team05.petmeeting.domain.animal.repository;

import com.team05.petmeeting.domain.animal.entity.Animal;
import org.springframework.data.domain.Page;

public interface AnimalRepositoryCustom {

    Page<Animal> getAnimals(
            String region,
            String kind,
            String processState
    );


}
