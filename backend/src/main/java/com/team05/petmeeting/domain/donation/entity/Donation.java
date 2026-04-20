package com.team05.petmeeting.domain.donation.entity;

import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.donation.enums.DonationStatus;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="donations")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Donation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="campaign_id")
    Campaign campaign;

    @Column(unique = true)
    String impUid;

    @Column(unique = true)
    String merchantUid;

    int amount;

    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.PENDING;

    @Builder
    public Donation(User user, Campaign campaign,
                    String merchantUid, int amount) {
        this.user = user;
        this.campaign = campaign;
        this.merchantUid = merchantUid;
        this.amount = amount;
    }

    public void complete(String impUid) {
        this.impUid = impUid;
        this.status = DonationStatus.PAID;
    }

    public void fail() {
        this.status = DonationStatus.FAILED;
    }
}
