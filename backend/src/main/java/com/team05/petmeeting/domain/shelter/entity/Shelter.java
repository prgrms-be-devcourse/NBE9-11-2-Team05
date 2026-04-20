package com.team05.petmeeting.domain.shelter.entity;

import com.team05.petmeeting.domain.campaign.entity.Campaign;
import com.team05.petmeeting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "shelters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Shelter {

    @Id
    @Column(name = "care_reg_no")
    String careRegNo;  // primary key, from 외부 api

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    User user;

    @Column(name = "care_nm")
    String careNm;

    @Column(name="care_tel")
    String careTel;

    @Column(name = "care_addr")
    String careAddr;

    @Column(name = "care_owner_nm")
    String careOwnerNm;

    @Column(name = "org_nm")
    String orgNm;

    @Column(name = "upd_at")
    LocalDateTime updAt;  // 외부 api 업데이트 시간

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "shelter", fetch = FetchType.LAZY)
    private List<Campaign> campaigns = new ArrayList<>();

    @Builder
    public Shelter(String careRegNo, User user, String careNm,
                   String careTel, String careAddr,
                   String careOwnerNm, String orgNm, LocalDateTime updAt) {
        this.careRegNo = careRegNo;
        this.user = user;
        this.careNm = careNm;
        this.careTel = careTel;
        this.careAddr = careAddr;
        this.careOwnerNm = careOwnerNm;
        this.orgNm = orgNm;
        this.updAt = updAt;
    }
}
