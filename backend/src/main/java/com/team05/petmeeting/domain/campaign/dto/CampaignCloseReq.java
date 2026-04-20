package com.team05.petmeeting.domain.campaign.dto;

import com.team05.petmeeting.domain.campaign.enums.CampaignStatus;

public record CampaignCloseReq (
        CampaignStatus status
) {

}
