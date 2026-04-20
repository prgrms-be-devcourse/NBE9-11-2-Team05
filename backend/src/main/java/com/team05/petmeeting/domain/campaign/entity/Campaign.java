package com.team05.petmeeting.domain.campaign.entity;

import com.team05.petmeeting.domain.campaign.enums.CampaignStatus;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import com.team05.petmeeting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="campaigns")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_reg_no")
    Shelter shelter;

    private String title;

    private int targetAmount;

    private int currentAmount;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status = CampaignStatus.ACTIVE;

    @Version
    private Long version;

    @Builder
    public Campaign(Shelter shelter, String title, int targetAmount) {
        this.shelter = shelter;
        this.title = title;
        this.targetAmount = targetAmount;
    }

    public void addAmount(int amount) {
        this.currentAmount += amount;
        if (this.currentAmount >= this.targetAmount) {
            this.status = CampaignStatus.COMPLETE;
        }
    }

    public void close() {
        this.status = CampaignStatus.CLOSED;
    }
}
