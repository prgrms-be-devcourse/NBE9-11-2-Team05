package com.team05.petmeeting.global.monitoring;

import com.team05.petmeeting.domain.animal.service.AnimalSyncMetricsRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AnimalSyncMicrometerRecorder implements AnimalSyncMetricsRecorder {
    private final AtomicInteger initialCurrentBatchSaved = new AtomicInteger();
    private final AtomicLong initialCurrentRunSaved = new AtomicLong();
    private final AtomicLong initialLastCompletedSaved = new AtomicLong();
    private final AtomicInteger updateCurrentBatchSaved = new AtomicInteger();
    private final AtomicLong updateCurrentRunSaved = new AtomicLong();
    private final AtomicLong updateLastCompletedSaved = new AtomicLong();
    private final AtomicLong initialLastRunAtMillis = new AtomicLong();
    private final AtomicLong updateLastRunAtMillis = new AtomicLong();
    private final Counter initialSavedTotalCounter;
    private final Counter updateSavedTotalCounter;

    public AnimalSyncMicrometerRecorder(MeterRegistry meterRegistry) {
        this.initialSavedTotalCounter = Counter.builder("animal_sync_initial_saved_total")
                .description("Total number of animals saved during initial sync")
                .register(meterRegistry);
        this.updateSavedTotalCounter = Counter.builder("animal_sync_update_saved_total")
                .description("Total number of animals saved or updated during update sync")
                .register(meterRegistry);

        Gauge.builder("animal_sync_initial_current_batch_saved", initialCurrentBatchSaved, AtomicInteger::get)
                .description("Animals saved in the latest initial sync batch")
                .register(meterRegistry);
        Gauge.builder("animal_sync_initial_current_run_saved", initialCurrentRunSaved, AtomicLong::get)
                .description("Animals saved in the current initial sync run")
                .register(meterRegistry);
        Gauge.builder("animal_sync_initial_last_completed_saved", initialLastCompletedSaved, AtomicLong::get)
                .description("Animals saved in the most recent completed initial sync run")
                .register(meterRegistry);
        Gauge.builder("animal_sync_update_current_batch_saved", updateCurrentBatchSaved, AtomicInteger::get)
                .description("Animals saved in the latest update sync batch")
                .register(meterRegistry);
        Gauge.builder("animal_sync_update_current_run_saved", updateCurrentRunSaved, AtomicLong::get)
                .description("Animals saved in the current update sync run")
                .register(meterRegistry);
        Gauge.builder("animal_sync_update_last_completed_saved", updateLastCompletedSaved, AtomicLong::get)
                .description("Animals saved in the most recent completed update sync run")
                .register(meterRegistry);

        TimeGauge.builder("animal_sync_initial_last_run_epoch_seconds", initialLastRunAtMillis, java.util.concurrent.TimeUnit.MILLISECONDS, value -> value.doubleValue() / 1000.0)
                .description("Epoch seconds of the most recent initial sync completion")
                .register(meterRegistry);
        TimeGauge.builder("animal_sync_update_last_run_epoch_seconds", updateLastRunAtMillis, java.util.concurrent.TimeUnit.MILLISECONDS, value -> value.doubleValue() / 1000.0)
                .description("Epoch seconds of the most recent update sync completion")
                .register(meterRegistry);
    }

    @Override
    public void startInitialSync() {
        initialCurrentBatchSaved.set(0);
        initialCurrentRunSaved.set(0);
    }

    @Override
    public void recordInitialBatchSaved(int savedCount) {
        initialCurrentBatchSaved.set(savedCount);
        initialCurrentRunSaved.addAndGet(savedCount);
        initialSavedTotalCounter.increment(savedCount);
    }

    @Override
    public void completeInitialSync(int totalSavedCount) {
        initialCurrentBatchSaved.set(0);
        initialCurrentRunSaved.set(totalSavedCount);
        initialLastCompletedSaved.set(totalSavedCount);
        initialLastRunAtMillis.set(System.currentTimeMillis());
    }

    @Override
    public void startUpdateSync() {
        updateCurrentBatchSaved.set(0);
        updateCurrentRunSaved.set(0);
    }

    @Override
    public void recordUpdateBatchSaved(int savedCount) {
        updateCurrentBatchSaved.set(savedCount);
        updateCurrentRunSaved.addAndGet(savedCount);
        updateSavedTotalCounter.increment(savedCount);
    }

    @Override
    public void completeUpdateSync(int totalSavedCount) {
        updateCurrentBatchSaved.set(0);
        updateCurrentRunSaved.set(totalSavedCount);
        updateLastCompletedSaved.set(totalSavedCount);
        updateLastRunAtMillis.set(System.currentTimeMillis());
    }
}
