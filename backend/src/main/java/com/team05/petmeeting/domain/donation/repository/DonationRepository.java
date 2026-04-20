package com.team05.petmeeting.domain.donation.repository;

import com.team05.petmeeting.domain.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
