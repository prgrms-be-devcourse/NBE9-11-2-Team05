package com.team05.petmeeting.domain.animal.service;

public interface AnimalSyncMetricsRecorder {
    void startInitialSync();
    void recordInitialBatchSaved(int savedCount);
    void completeInitialSync(int totalSavedCount);
    void startUpdateSync();
    void recordUpdateBatchSaved(int savedCount);
    void completeUpdateSync(int totalSavedCount);
}
