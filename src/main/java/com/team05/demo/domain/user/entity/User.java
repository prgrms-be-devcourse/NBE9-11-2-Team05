package com.team05.demo.domain.user.entity;

import com.team05.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private int dailyHeartCount;

    @Column(nullable = false)
    private LocalDate lastHeartResetDate;

    // 매일 자정마다 응원 횟수 초기화
    public void resetDailyHeartCountIfNeeded() {
        if (!this.lastHeartResetDate.equals(LocalDate.now())) {
            this.dailyHeartCount = 0;
            this.lastHeartResetDate = LocalDate.now();
        }
    }

    // 응원사용
    public void useDailyCheer() {
        this.dailyHeartCount++;
    }

}
