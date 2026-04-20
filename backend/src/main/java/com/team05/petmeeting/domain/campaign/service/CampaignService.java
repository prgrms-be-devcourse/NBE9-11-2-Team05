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

    public CampaignCreateRes createCampaign(String shelterId, Long userId, CampaignReq req){
        Shelter shelter = shelterService.findById(shelterId);
        if (!shelter.isManagedBy(userId)) {
            throw new BusinessException(CampaignErrorCode.UNAUTHORIZED_SHELTER);
        }

        if (campaignRepository.existsByShelter_CareRegNoAndStatus(shelterId, CampaignStatus.ACTIVE)) {
            throw new BusinessException(CampaignErrorCode.CAMPAIGN_ALREADY_EXISTS);
        }

        Campaign campaign = Campaign.create(shelter, req.title(), req.amount());
        campaignRepository.save(campaign);
        return CampaignCreateRes.from(campaign);
    }

    public CampaignDetailRes getCampaign(String shelterId){
        Shelter shelter = shelterService.findById(shelterId);
        return campaignRepository.findByShelter(shelter)
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
