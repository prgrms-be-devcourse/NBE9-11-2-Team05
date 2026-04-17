package com.team05.petmeeting.domain.animal.entity;

import com.team05.petmeeting.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "sync_state")
@NoArgsConstructor
public class SyncState extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private AnimalSyncType syncType;

    @Column
    private LocalDateTime lastUpdatedAt;

    private SyncState(AnimalSyncType syncType) {
        this.syncType = syncType;
    }

    public static SyncState create(AnimalSyncType syncType) {
        return new SyncState(syncType);
    }

    public void updateLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
