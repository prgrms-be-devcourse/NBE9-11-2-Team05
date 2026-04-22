package com.team05.petmeeting.domain.adoption.service;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionDetailResponse;
import com.team05.petmeeting.domain.adoption.entity.AdoptionApplication;
import com.team05.petmeeting.domain.adoption.repository.AdoptionApplicationRepository;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import com.team05.petmeeting.domain.shelter.repository.ShelterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdoptionAdminService {

    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final ShelterRepository shelterRepository;

    // careRegNo 보호소의 관리자인지 확인한 뒤 해당 보호소의 입양 신청 목록만 반환한다.
    @Transactional(readOnly = true)
    public List<AdoptionApplyResponse> getManagedShelterApplications(Long userId, String careRegNo) {
        validateShelterManager(userId, careRegNo);

        return adoptionApplicationRepository.findByAnimal_Shelter_CareRegNo(careRegNo).stream()
                .map(this::toResponse)
                .toList();
    }

    // careRegNo 보호소의 관리자인지 확인한 뒤 담당 보호소 신청 상세만 반환한다.
    @Transactional(readOnly = true)
    public AdoptionDetailResponse getManagedShelterApplicationDetail(Long userId, String careRegNo, Long applicationId) {
        validateShelterManager(userId, careRegNo);

        AdoptionApplication application = adoptionApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("입양 신청 내역이 없습니다."));

        if (!isShelterApplication(application, careRegNo)) {
            throw new RuntimeException("담당 보호소의 입양 신청만 조회할 수 있습니다.");
        }

        return toDetailResponse(application);
    }

    // 사용자가 careRegNo 보호소의 관리자인지 확인한다.
    private void validateShelterManager(Long userId, String careRegNo) {
        Shelter shelter = shelterRepository.findById(careRegNo)
                .orElseThrow(() -> new RuntimeException("보호소를 찾을 수 없습니다."));

        if (!shelter.isManagedBy(userId)) {
            throw new RuntimeException("해당 보호소의 관리자가 아닙니다.");
        }
    }

    // 신청 동물의 보호소가 요청한 careRegNo 보호소인지 확인한다.
    private boolean isShelterApplication(AdoptionApplication application, String careRegNo) {
        Shelter shelter = application.getAnimal().getShelter();
        return shelter != null && shelter.getCareRegNo().equals(careRegNo);
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
