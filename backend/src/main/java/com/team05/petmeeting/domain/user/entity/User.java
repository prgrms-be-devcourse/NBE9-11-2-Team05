package com.team05.petmeeting.domain.user.entity;

import com.team05.petmeeting.domain.user.role.Role;
import com.team05.petmeeting.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String realname;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

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

    public static User create(
            String username,
            String password,
            String nickname,
            String realname
    ) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.profileImageUrl = "";
        user.realname = realname;
        user.role = Role.USER;
        user.dailyHeartCount = 0;
        user.lastHeartResetDate = LocalDate.now();
        return user;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;
    }

}
