package com.team05.petmeeting.domain.donation.service;

import com.team05.petmeeting.domain.campaign.service.CampaignService;
import com.team05.petmeeting.domain.donation.dto.PrepareReq;
import com.team05.petmeeting.domain.donation.dto.PrepareRes;
import com.team05.petmeeting.domain.donation.entity.Donation;
import com.team05.petmeeting.domain.donation.enums.DonationStatus;
import com.team05.petmeeting.domain.donation.repository.DonationRepository;
import com.team05.petmeeting.domain.user.dto.profile.UserDonationRes;
import com.team05.petmeeting.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final CampaignService campaignService;
    private final UserService userService;

    // 결제 준비 merchantUid 발급
    public PrepareRes prepare(Long userId, PrepareReq req) {
        String merchantUid="";
        return new PrepareRes(merchantUid, req.amount());
    }

    // 결제 완료 + 검증
//    public UserDonationRes donate(Long userId, Donation donation) {
        // todo : 검증 로직
//    }

    // 웹훅 처리
    public void handleWebhook(String impUid, String merchantUid) {
        // todo : webhook 검증 로직
    }

    // 내 후원 내역
    public UserDonationRes getMyDonations(Long userId){
        List<Donation> donations = donationRepository.findByUser_Id(userId);
        int totalAmount = donations.stream()
                .filter(d -> d.getStatus() == DonationStatus.PAID)
                .mapToInt(Donation::getAmount)
                .sum();
        return UserDonationRes.of(donations.size(), totalAmount, donations);
    }

}
