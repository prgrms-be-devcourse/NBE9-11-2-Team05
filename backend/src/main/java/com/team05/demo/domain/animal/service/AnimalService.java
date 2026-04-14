package com.team05.demo.domain.animal.service;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.errorCode.AnimalErrorCode;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public Animal findByAnimalId(Long animalId){
        return animalRepository.findById(animalId).orElseThrow(() -> new BusinessException(AnimalErrorCode.ANIMAL_NOT_FOUND));
    }
}
