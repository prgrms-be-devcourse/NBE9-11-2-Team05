package com.team05.petmeeting.domain.adoption.service;

import com.team05.petmeeting.domain.adoption.dto.request.AdoptionApplyRequest;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionDetailResponse;
import com.team05.petmeeting.domain.adoption.entity.AdoptionApplication;
import com.team05.petmeeting.domain.adoption.repository.AdoptionApplicationRepository;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;

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

    public AdoptionApplyResponse applyApplication(Long userId, Long animalId, AdoptionApplyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        AdoptionApplication application = AdoptionApplication.create(
                user,
                animal,
                request.getApplyReason(),
                request.getApplyTel()
        );

        AdoptionApplication saved = adoptionApplicationRepository.save(application);
        return toResponse(saved);
    }
}
