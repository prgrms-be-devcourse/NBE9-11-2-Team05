package com.team05.demo.domain.feed.service;

import com.team05.demo.domain.feed.dto.FeedRequest;
import com.team05.demo.domain.feed.dto.FeedRes;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.feed.repository.FeedRepository;
import com.team05.demo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team05.demo.domain.feed.errorCode.FeedErrorCode;
import com.team05.demo.global.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    @Transactional
    public FeedRes write(FeedRequest request, User user){
        Feed feed = new Feed(user, request.category(), request.title(), request.content(), request.imageUrl());
        feedRepository.save(feed);
        return new FeedRes(feed);
    }

    @Transactional
    public FeedRes modify(Long feedId, FeedRequest request, User user){
        Feed feed = feedRepository.findById(feedId)
                        .orElseThrow(()-> new BusinessException(FeedErrorCode.FEED_NOT_FOUND));
        feed.checkModify(user);
        feed.update(request.category(),request.title(),request.content(),request.imageUrl());
        return new FeedRes(feed);
    }

   @Transactional
    public void delete(Long feedId, User user){
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(()->new BusinessException(FeedErrorCode.FEED_NOT_FOUND));
        feed.checkDelete(user);
        feedRepository.delete(feed);
    }

    public Feed findByFeedId(Long id) {
        return feedRepository.findById(id).orElseThrow(()-> new BusinessException(FeedErrorCode.FEED_NOT_FOUND));
    }
}
