package com.team05.petmeeting.domain.adoption.service;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionDetailResponse;
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
//=======================================================================================================================//

    public AdoptionDetailResponse getApplicationDetail(Long userId, Long applicationId) {
        AdoptionApplication application = adoptionApplicationRepository
                .findByIdAndUser_Id(applicationId, userId)
                .orElseThrow(() -> new RuntimeException("입양 신청 내역이 없습니다."));

        return toDetailResponse(application);
    }


    private AdoptionDetailResponse toDetailResponse(AdoptionApplication application) {
        Animal animal = application.getAnimal();

        AdoptionDetailResponse.AnimalInfo animalInfo =
                new AdoptionDetailResponse.AnimalInfo(
                        animal.getDesertionNo(),
                        animal.getSpecialMark(),
                        animal.getCareNm(),
                        animal.getCareOwerNm(),
                        animal.getCareTel(),
                        animal.getCareAddr()
                );

        return new AdoptionDetailResponse(
                application.getId(),
                application.getStatus(),
                application.getApplyReason(),
                application.getCreatedAt(),
                application.getReviewedAt(),
                application.getRejectionReason(),
                application.getApplyTel(),
                animalInfo
        );
    }
    //=======================================================================================================================//
}
