package com.team05.petmeeting.domain.animal.service;

public interface InitialSyncProgressNotifier {
    void notifyProgress(int previousSavedCount, int currentSavedCount);
}
