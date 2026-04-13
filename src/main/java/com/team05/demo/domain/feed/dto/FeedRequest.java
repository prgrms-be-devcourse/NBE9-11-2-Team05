package com.team05.demo.domain.feed.dto;

import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.enums.FeedCategory;

public record FeedRequest(
        FeedCategory category,
        String title,
        String content,
        String imageUrl
) {
}
