package com.team05.petmeeting.domain.adoption.service;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.entity.AdoptionApplication;
import com.team05.petmeeting.domain.adoption.repository.AdoptionApplicationRepository;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdoptionAdminService {

    private final AdoptionApplicationRepository adoptionApplicationRepository;

    @Transactional(readOnly = true)
    public List<AdoptionApplyResponse> getManagedShelterApplications(Long userId) {
        return adoptionApplicationRepository.findAll().stream()
                .filter(application -> isManagedShelterApplication(application, userId))
                .map(this::toResponse)
                .toList();
    }

    private boolean isManagedShelterApplication(AdoptionApplication application, Long userId) {
        Shelter shelter = application.getAnimal().getShelter();
        return shelter != null && shelter.isManagedBy(userId);
    }

    private AdoptionApplyResponse toResponse(AdoptionApplication application) {
        Animal animal = application.getAnimal();

        AdoptionApplyResponse.AnimalInfo animalInfo = new AdoptionApplyResponse.AnimalInfo(
                animal.getDesertionNo(),
                animal.getKindFullNm(),
                animal.getCareNm(),
                animal.getCareOwnerNm()
        );

        return new AdoptionApplyResponse(
                application.getId(),
                application.getStatus(),
                animalInfo
        );
    }
}
