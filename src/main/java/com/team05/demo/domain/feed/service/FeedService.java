package com.team05.demo.domain.feed.service;

import com.team05.demo.domain.feed.dto.FeedRequest;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.repository.FeedRepository;
import com.team05.demo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    @Transactional
    public Feed write(FeedRequest request, User user){
        Feed feed = new Feed(user, request.category(), request.title(), request.content(), request.imageUrl());
        return feedRepository.save(feed);
    }
}
