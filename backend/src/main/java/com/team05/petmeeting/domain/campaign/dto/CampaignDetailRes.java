package com.team05.petmeeting.domain.campaign.dto;

import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.campaign.enums.CampaignStatus;

public record CampaignDetailRes (
        Long id,
        String title,
        int targetAmount,
        int currentAmount,
        CampaignStatus status
){
    public static CampaignDetailRes from(Campaign campaign) {
        return new CampaignDetailRes(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getTargetAmount(),
                campaign.getCurrentAmount(),
                campaign.getStatus()
        );
    }
}
