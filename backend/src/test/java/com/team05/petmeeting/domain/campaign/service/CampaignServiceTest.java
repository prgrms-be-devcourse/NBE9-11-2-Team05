package com.team05.petmeeting.domain.campaign.service;

import com.team05.petmeeting.domain.animal.service.AnimalExternalService;
import com.team05.petmeeting.domain.campaign.dto.CampaignCreateReq;
import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.campaign.enums.CampaignStatus;
import com.team05.petmeeting.domain.campaign.repository.CampaignRepository;
import com.team05.petmeeting.domain.shelter.dto.ShelterCommand;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import com.team05.petmeeting.domain.shelter.service.ShelterService;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CampaignServiceTest {
    @MockitoBean
    AnimalExternalService animalExternalService;  // 외부 의존 mock 처리

    @Autowired
    CampaignService campaignService;
    @Autowired CampaignRepository campaignRepository;
    @Autowired ShelterService shelterService;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("캠페인 생성 성공")
    public void createCampaign() {
        // given - User 먼저 만들어야 해요
        User user = userRepository.save(User.create("test@test.com", "password", "nickname", "realname"));

        // Shelter 만들고 user 할당
        ShelterCommand cmd = new ShelterCommand(
                "123", "보호소1", "010", "주소", "소유자", "기관", LocalDateTime.now()
        );
        shelterService.createOrUpdateShelter(cmd);
        Shelter shelter = shelterService.findById("123");
        shelter.assignUser(user);

        // when
        campaignService.createCampaign("123", user.getId(), new CampaignCreateReq("사료 후원", 1000000));

        // then
        Campaign result = campaignRepository
                .findByShelter_CareRegNoAndStatus("123", CampaignStatus.ACTIVE)
                .orElseThrow();
        assertThat(result.getTitle()).isEqualTo("사료 후원");
    }
}
