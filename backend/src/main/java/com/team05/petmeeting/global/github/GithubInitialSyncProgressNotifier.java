package com.team05.petmeeting.global.github;

import com.team05.petmeeting.domain.animal.service.InitialSyncProgressNotifier;
import com.team05.petmeeting.global.config.GithubInitialSyncNotificationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubInitialSyncProgressNotifier implements InitialSyncProgressNotifier {
    private final GithubInitialSyncNotificationProperties properties;

    @Override
    public void notifyProgress(int previousSavedCount, int currentSavedCount) {
        int threshold = properties.getNotificationThreshold();

        if (threshold <= 0 || currentSavedCount <= previousSavedCount || currentSavedCount < threshold) {
            return;
        }

        GithubInitialSyncNotificationProperties.Github github = properties.getGithub();
        if (!github.isEnabled()) {
            return;
        }

        if (!isConfigured(github)) {
            log.warn("GitHub initial sync notification skipped due to incomplete configuration.");
            return;
        }

        int previousMilestone = previousSavedCount / threshold;
        int currentMilestone = currentSavedCount / threshold;

        for (int milestone = previousMilestone + 1; milestone <= currentMilestone; milestone++) {
            createIssue(github, milestone * threshold, currentSavedCount);
        }
    }

    private void createIssue(
            GithubInitialSyncNotificationProperties.Github github,
            int milestoneCount,
            int currentSavedCount
    ) {
        String issuesUrl = "https://api.github.com/repos/%s/%s/issues"
                .formatted(github.getOwner(), github.getRepo());

        String title = "Initial Animal Sync Milestone Reached - %,d Records".formatted(milestoneCount);
        String body = """
                The initial animal sync is currently in progress.

                Milestone reached: %,d records
                Current saved count: %,d records
                """.formatted(milestoneCount, currentSavedCount);

        try {
            RestClient.create()
                    .post()
                    .uri(issuesUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + github.getToken())
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "title", title,
                            "body", body
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException e) {
            log.warn("GitHub initial sync notification failed: milestoneCount={}, currentSavedCount={}",
                    milestoneCount, currentSavedCount, e);
        }
    }

    private boolean isConfigured(GithubInitialSyncNotificationProperties.Github github) {
        return StringUtils.hasText(github.getOwner())
                && StringUtils.hasText(github.getRepo())
                && StringUtils.hasText(github.getToken());
    }
}
