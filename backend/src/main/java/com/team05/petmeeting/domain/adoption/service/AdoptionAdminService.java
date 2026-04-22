package com.team05.petmeeting.domain.adoption.service;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionDetailResponse;
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

    // 로그인한 관리자가 담당하는 보호소의 입양 신청만 필터링해서 목록으로 반환한다.
    @Transactional(readOnly = true)
    public List<AdoptionApplyResponse> getManagedShelterApplications(Long userId) {
        return adoptionApplicationRepository.findAll().stream()
                .filter(application -> isManagedShelterApplication(application, userId))
                .map(this::toResponse)
                .toList();
    }

    // 입양 신청 상세 조회 전 담당 보호소 신청인지 검증한다.
    @Transactional(readOnly = true)
    public AdoptionDetailResponse getManagedShelterApplicationDetail(Long userId, Long applicationId) {
        AdoptionApplication application = adoptionApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("입양 신청 내역이 없습니다."));

        if (!isManagedShelterApplication(application, userId)) {
            throw new RuntimeException("담당 보호소의 입양 신청만 조회할 수 있습니다.");
        }

        return toDetailResponse(application);
    }

    // 신청 동물의 보호소가 로그인한 관리자의 담당 보호소인지 확인한다.
    private boolean isManagedShelterApplication(AdoptionApplication application, Long userId) {
        Shelter shelter = application.getAnimal().getShelter();
        return shelter != null && shelter.isManagedBy(userId);
    }

    // 관리자 목록 조회에 필요한 최소 신청 정보로 변환한다.
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

    // 관리자 상세 조회에 필요한 신청, 연락처, 심사, 동물 정보를 함께 변환한다.
    private AdoptionDetailResponse toDetailResponse(AdoptionApplication application) {
        Animal animal = application.getAnimal();

        AdoptionDetailResponse.AnimalInfo animalInfo = new AdoptionDetailResponse.AnimalInfo(
                animal.getDesertionNo(),
                animal.getSpecialMark(),
                animal.getCareNm(),
                animal.getCareOwnerNm(),
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
}
