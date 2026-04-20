package com.team05.petmeeting.domain.campaign.service;

import com.team05.petmeeting.domain.campaign.dto.CampaignCreateRes;
import com.team05.petmeeting.domain.campaign.dto.CampaignDetailRes;
import com.team05.petmeeting.domain.campaign.dto.CampaignReq;
import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.campaign.enums.CampaignStatus;
import com.team05.petmeeting.domain.campaign.errorCode.CampaignErrorCode;
import com.team05.petmeeting.domain.campaign.repository.CampaignRepository;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import com.team05.petmeeting.domain.shelter.service.ShelterService;
import com.team05.petmeeting.global.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ShelterService shelterService;

    public CampaignCreateRes createCampaign(CampaignReq req){
        if (campaignRepository.existsByStatus(CampaignStatus.ACTIVE)){
            throw new BusinessException(CampaignErrorCode.CAMPAIGN_ALREADY_EXISTS);
        }
        Shelter shelter = shelterService.findById(req.shelterId());
        Campaign campaign = Campaign.create(shelter, req.title(), req.amount());
        campaignRepository.save(campaign);
        return CampaignCreateRes.from(campaign);
    }

    public CampaignDetailRes getCampaign(String shelterId){
        return campaignRepository.findByShelter(shelterId)
                .map(CampaignDetailRes::from)
                .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));
    }

    public void closeCampaign(Long id){
        Campaign campaign = getCampaignOrThrow(id);
        if (campaign.getStatus() != CampaignStatus.ACTIVE){
            throw new BusinessException(CampaignErrorCode.CAMPAIGN_CLOSED);
        }
        campaign.close();
    }

    private Campaign getCampaignOrThrow(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));
    }
}
