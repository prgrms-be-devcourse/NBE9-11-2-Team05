package com.team05.petmeeting.domain.notification.entity;

import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends BaseEntity { // 알림 엔티티 (target_url 포함)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, name = "target_url")
    private String targetUrl;

    @Column(nullable = false, name = "is_read")
    private boolean isRead = false;

    public static Notification create(User user, String message, String targetUrl) {
        Notification notification = new Notification();
        notification.user = user;
        notification.message = message;
        notification.targetUrl = targetUrl;
        notification.isRead = false;
        return notification;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}
