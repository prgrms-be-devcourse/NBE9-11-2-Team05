package com.team05.petmeeting.domain.feed.dto;

import com.team05.petmeeting.domain.feed.enums.FeedCategory;

public record FeedRequest(
        FeedCategory category,
        String title,
        String content,
        String imageUrl
) {
}
