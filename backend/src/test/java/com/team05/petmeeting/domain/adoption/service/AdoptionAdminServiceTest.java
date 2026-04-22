package com.team05.petmeeting.domain.adoption.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionDetailResponse;
import com.team05.petmeeting.domain.adoption.entity.AdoptionApplication;
import com.team05.petmeeting.domain.adoption.entity.AdoptionStatus;
import com.team05.petmeeting.domain.adoption.repository.AdoptionApplicationRepository;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.shelter.dto.ShelterCommand;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.global.entity.BaseEntity;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdoptionAdminServiceTest {

    @InjectMocks
    AdoptionAdminService adoptionAdminService;

    @Mock
    AdoptionApplicationRepository adoptionApplicationRepository;

    @Test
    @DisplayName("담당 보호소의 입양 신청 목록만 조회한다")
    void getManagedShelterApplications() throws Exception {
        User manager = createUser(1L, "manager@test.com");
        User otherManager = createUser(2L, "other-manager@test.com");
        User applicant = createUser(3L, "applicant@test.com");

        AdoptionApplication managedApplication = createApplication(
                1L,
                applicant,
                createAnimal("A-001", "담당보호소", createShelter("S-001", "담당보호소", manager))
        );
        AdoptionApplication otherApplication = createApplication(
                2L,
                applicant,
                createAnimal("A-002", "다른보호소", createShelter("S-002", "다른보호소", otherManager))
        );
        when(adoptionApplicationRepository.findAll())
                .thenReturn(List.of(managedApplication, otherApplication));

        List<AdoptionApplyResponse> responses =
                adoptionAdminService.getManagedShelterApplications(manager.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getApplicationId()).isEqualTo(managedApplication.getId());
        assertThat(responses.get(0).getStatus()).isEqualTo(AdoptionStatus.Processing);
        assertThat(responses.get(0).getAnimalInfo().getCareNm()).isEqualTo("담당보호소");
    }

    @Test
    @DisplayName("담당 보호소의 입양 신청 상세를 조회한다")
    void getManagedShelterApplicationDetail() throws Exception {
        User manager = createUser(1L, "manager@test.com");
        User applicant = createUser(2L, "applicant@test.com");
        Animal animal = createAnimal("A-001", "담당보호소", createShelter("S-001", "담당보호소", manager));
        AdoptionApplication application = createApplication(1L, applicant, animal);
        when(adoptionApplicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        AdoptionDetailResponse response =
                adoptionAdminService.getManagedShelterApplicationDetail(manager.getId(), application.getId());

        assertThat(response.getApplicationId()).isEqualTo(application.getId());
        assertThat(response.getStatus()).isEqualTo(AdoptionStatus.Processing);
        assertThat(response.getApplyReason()).isEqualTo("입양하고 싶습니다.");
        assertThat(response.getApplyTel()).isEqualTo("010-1234-5678");
        assertThat(response.getAnimalInfo().getDesertionNo()).isEqualTo("A-001");
        assertThat(response.getAnimalInfo().getCareNm()).isEqualTo("담당보호소");
    }

    @Test
    @DisplayName("다른 보호소의 입양 신청 상세는 조회할 수 없다")
    void getManagedShelterApplicationDetail_otherShelter() throws Exception {
        User manager = createUser(1L, "manager@test.com");
        User otherManager = createUser(2L, "other-manager@test.com");
        User applicant = createUser(3L, "applicant@test.com");
        Animal animal = createAnimal("A-001", "다른보호소", createShelter("S-001", "다른보호소", otherManager));
        AdoptionApplication application = createApplication(1L, applicant, animal);
        when(adoptionApplicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        assertThatThrownBy(() ->
                adoptionAdminService.getManagedShelterApplicationDetail(manager.getId(), application.getId())
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("담당 보호소의 입양 신청만 조회할 수 있습니다.");
    }

    private User createUser(Long id, String email) throws Exception {
        User user = User.create(email, email, "홍길동");
        setId(user, id);
        return user;
    }

    private Shelter createShelter(String careRegNo, String careNm, User manager) {
        Shelter shelter = Shelter.create(new ShelterCommand(
                careRegNo,
                careNm,
                "010-0000-0000",
                "서울시",
                "보호소장",
                "서울시",
                LocalDateTime.now()
        ));
        shelter.assignUser(manager);
        return shelter;
    }

    private Animal createAnimal(String desertionNo, String careNm, Shelter shelter) {
        Animal animal = Animal.builder()
                .desertionNo(desertionNo)
                .processState("보호중")
                .stateGroup(0)
                .kindFullNm("믹스견")
                .specialMark("특이사항 없음")
                .careNm(careNm)
                .careOwnerNm("보호소장")
                .careTel("010-0000-0000")
                .careAddr("서울시")
                .totalCheerCount(0)
                .build();
        animal.assignShelter(shelter);
        return animal;
    }

    private AdoptionApplication createApplication(Long id, User applicant, Animal animal) throws Exception {
        AdoptionApplication application = AdoptionApplication.create(
                applicant,
                animal,
                "입양하고 싶습니다.",
                "010-1234-5678"
        );
        setId(application, id);
        return application;
    }

    private void setId(BaseEntity entity, Long id) throws Exception {
        Field idField = BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}
