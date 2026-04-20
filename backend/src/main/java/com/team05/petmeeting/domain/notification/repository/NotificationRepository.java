package com.team05.petmeeting.domain.notification.repository;

import com.team05.petmeeting.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> { // 알림 내역 조회 및 저장
}
