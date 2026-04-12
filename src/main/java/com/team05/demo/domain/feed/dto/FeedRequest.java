package com.team05.demo.domain.feed.dto;

import com.team05.demo.domain.feed.entity.Feed;

public record FeedRequest(

        String content,
        String imageUrl
) {
}
