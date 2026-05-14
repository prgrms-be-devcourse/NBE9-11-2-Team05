package com.team05.petmeeting.domain.donation.service;

import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.campaign.service.CampaignService;
import com.team05.petmeeting.domain.donation.dto.CompleteReq;
import com.team05.petmeeting.domain.donation.dto.CompleteRes;
import com.team05.petmeeting.domain.donation.dto.PrepareReq;
import com.team05.petmeeting.domain.donation.dto.PrepareRes;
import com.team05.petmeeting.domain.donation.entity.Donation;
import com.team05.petmeeting.domain.donation.enums.DonationStatus;
import com.team05.petmeeting.domain.donation.errorCode.DonationErrorCode;
import com.team05.petmeeting.domain.donation.repository.DonationRepository;
import com.team05.petmeeting.domain.user.dto.profile.UserDonationRes;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.service.UserService;
import com.team05.petmeeting.global.exception.BusinessException;
import io.portone.sdk.server.PortOneClient;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final CampaignService campaignService;
    private final UserService userService;
    private final PortOneClient portOne;

    @Value("${portone.store-id}")
    private String storeId;
    // 결제 준비 paymentId 발급
    public PrepareRes prepare(Long userId, PrepareReq req) {
        String paymentId = "payment-" + UUID.randomUUID();
        User user = userService.findById(userId);
        Campaign campaign = campaignService.findById(req.campaignId());

        Donation donation = Donation.create(user, campaign, paymentId, req.amount());
        donationRepository.save(donation);

        return new PrepareRes(paymentId, req.amount());
    }

    // 결제 완료 + 검증
    public CompleteRes donate(Long userId, CompleteReq req) {
        // todo : 검증 로직
        Donation donation = donationRepository.findByPaymentId(req.paymentId())
                .orElseThrow(() -> new BusinessException(DonationErrorCode.DONATION_NOT_FOUND));

        // 결제 단건 조회
        Payment payment = null;
        try {  payment = portOne.getPayment().getPayment(req.paymentId()).get();}
        catch (Exception e) {
            log.error("포트원 결제 내역 조회 실패: {}", e.getMessage());
            throw new RuntimeException("결제 검증에 실패했습니다.");
        }

//        payment

        // [검증 1] 결제 상태 확인 (V2 기준 Paid 상태인지 확인)
        if (!payment) { // 실제 SDK의 Enum(ex: PaymentStatus.PAID)에 맞게 수정
            donation.fail(); // 예: status를 FAILED로 변경
            throw new IllegalStateException("완료되지 않은 결제입니다.");
        }

        // [검증 2] 결제 금액 위변조 확인
        int paidAmount = (int) ((PaidPayment) payment).getAmount().getTotal();
        if (paidAmount != donation.getAmount()) {
            donation.fail();
            // TODO: 금액이 위변조된 경우 포트원 API를 호출하여 결제 강제 취소(환불) 처리 권장
            throw new IllegalStateException("결제 금액 불일치. 결제가 취소됩니다.");
        }

        // 검증 성공 시 상태 업데이트
        donation.complete(req.paymentId()); // 예: status를 PAID로 변경하는 도메인 메서드

        return new CompleteRes(
                donation.getId(),
                donation.getAmount(),
                donation.getStatus(),
                donation.getCampaign().getId()
        );
    }

    // 웹훅 처리
    public void handleWebhook(String paymentId) {
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
