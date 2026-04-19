package com.team05.petmeeting.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "animal.sync")
public class GithubInitialSyncNotificationProperties {
    private int notificationThreshold = 100_000;
    private Github github = new Github();

    @Getter
    @Setter
    public static class Github {
        private boolean enabled;
        private String owner;
        private String repo;
        private int issueNumber;
        private String token;
    }
}
