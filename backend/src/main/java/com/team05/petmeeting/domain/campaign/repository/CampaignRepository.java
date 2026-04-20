package com.team05.petmeeting.domain.campaign.repository;

import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.campaign.enums.CampaignStatus;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findByShelter(Shelter shelter);

    boolean existsByShelter_CareRegNoAndStatus(String shelterId, CampaignStatus campaignStatus);

    Optional<Campaign> findByShelter_CareRegNoAndStatus(String shelterId, CampaignStatus campaignStatus);
}
