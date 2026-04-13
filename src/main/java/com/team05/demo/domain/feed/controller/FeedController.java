package com.team05.demo.domain.feed.controller;

import com.team05.demo.domain.feed.dto.FeedRequest;
import com.team05.demo.domain.feed.dto.FeedRes;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.service.FeedService;
import com.team05.demo.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {
    private final FeedService feedService;

    @PostMapping
    public RsData<FeedRes> write(@RequestBody FeedRequest request) {
        // JWT 구현 후 실제 user로 교체
        Feed feed = feedService.write(request, null);
        return new RsData<>(
                "201-1",
                "피드가 작성되었습니다.",
                new FeedRes(feed)
        );
    }
}
