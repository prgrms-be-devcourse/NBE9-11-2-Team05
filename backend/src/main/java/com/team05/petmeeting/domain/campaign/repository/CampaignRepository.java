package com.team05.petmeeting.domain.campaign.repository;

import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.campaign.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    boolean existsByStatus(CampaignStatus campaignStatus);

    Optional<Campaign> findByShelter(String shelterId);
}
