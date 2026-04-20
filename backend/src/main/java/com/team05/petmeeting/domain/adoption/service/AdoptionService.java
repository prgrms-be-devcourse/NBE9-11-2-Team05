package com.team05.petmeeting.domain.adoption.service;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.entity.AdoptionApplication;
import com.team05.petmeeting.domain.adoption.repository.AdoptionApplicationRepository;
import com.team05.petmeeting.domain.animal.entity.Animal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionApplicationRepository adoptionApplicationRepository;

    public List<AdoptionApplyResponse> getMyadoptions(Long userId) {
        return adoptionApplicationRepository.findByUser_Id(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AdoptionApplyResponse toResponse(AdoptionApplication application) {
        Animal animal = application.getAnimal();

        AdoptionApplyResponse.AnimalInfo animalInfo =
                new AdoptionApplyResponse.AnimalInfo(
                        animal.getDesertionNo(),
                        animal.getKindFullNm(),
                        animal.getCareNm(),
                        animal.getCareOwerNm()
                );

        return new AdoptionApplyResponse(
                application.getId(),
                application.getStatus(),
                animalInfo
        );
    }
}
