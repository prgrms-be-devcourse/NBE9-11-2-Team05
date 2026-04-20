package com.team05.petmeeting.domain.donation.service;

import com.team05.petmeeting.domain.donation.repository.DonationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;


}
