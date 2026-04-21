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

    // 사용자별 입양 신청 목록을 조회하고 목록 응답 DTO로 변환한다.
    public List<AdoptionApplyResponse> getMyAdoptions(Long userId) {
        return adoptionApplicationRepository.findByUser_Id(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    // 목록 조회용 입양 신청 엔터티를 간단한 응답 DTO로 변환한다.
    private AdoptionApplyResponse toResponse(AdoptionApplication application) {
        Animal animal = application.getAnimal();

        AdoptionApplyResponse.AnimalInfo animalInfo =
                new AdoptionApplyResponse.AnimalInfo(
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

    // 로그인한 사용자의 입양 신청 상세를 조회하고 상세 응답 DTO로 변환한다.
    public AdoptionDetailResponse getApplicationDetail(Long userId, Long applicationId) {
        AdoptionApplication application = adoptionApplicationRepository
                .findByIdAndUser_Id(applicationId, userId)
                .orElseThrow(() -> new RuntimeException("입양 신청 내역이 없습니다."));

        return toDetailResponse(application);
    }

    // 상세 조회용 입양 신청 엔터티를 상세 응답 DTO로 변환한다.
    private AdoptionDetailResponse toDetailResponse(AdoptionApplication application) {
        Animal animal = application.getAnimal();

        AdoptionDetailResponse.AnimalInfo animalInfo =
                new AdoptionDetailResponse.AnimalInfo(
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

    // 로그인한 사용자의 입양 신청을 저장하고 생성 결과를 응답 DTO로 반환한다.
    public AdoptionApplyResponse applyApplication(Long userId, Long animalId, AdoptionApplyRequest request) {
        if (adoptionApplicationRepository.existsByUser_IdAndAnimal_Id(userId, animalId)) {
            throw new RuntimeException("이미 입양 신청한 동물입니다.");
        }

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

    // 로그인한 사용자의 입양 신청을 조회한 뒤 본인 신청서만 삭제한다.
    public void cancelApplication(Long userId, Long applicationId) {
        AdoptionApplication application = adoptionApplicationRepository.findByIdAndUser_Id(applicationId, userId)
                .orElseThrow(() -> new RuntimeException("입양 신청 내역이 없습니다."));

        adoptionApplicationRepository.delete(application);
    }
}
