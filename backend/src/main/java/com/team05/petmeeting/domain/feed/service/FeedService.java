package com.team05.petmeeting.domain.feed.service;

import com.team05.petmeeting.domain.feed.dto.FeedListRes;
import com.team05.petmeeting.domain.feed.dto.FeedReq;
import com.team05.petmeeting.domain.feed.dto.FeedRes;
import com.team05.petmeeting.domain.feed.entity.Feed;
import com.team05.petmeeting.domain.feed.repository.FeedLikeRepository;
import com.team05.petmeeting.domain.feed.repository.FeedRepository;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team05.petmeeting.domain.feed.errorCode.FeedErrorCode;
import com.team05.petmeeting.global.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeedRes write(FeedReq request, User user) {
        Feed feed = new Feed(user, request.category(), request.title(), request.content(), request.imageUrl());
        feedRepository.save(feed);
        return new FeedRes(feed, 0);
    }

    @Transactional
    public FeedRes modify(Long feedId, FeedReq request, User user) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new BusinessException(FeedErrorCode.FEED_NOT_FOUND));
        feed.checkModify(user);
        feed.update(request.category(), request.title(), request.content(), request.imageUrl());
        int likeCount = (int) feedLikeRepository.countByFeed(feed);
        return new FeedRes(feed, likeCount);
    }

    @Transactional
    public void delete(Long feedId, User user) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new BusinessException(FeedErrorCode.FEED_NOT_FOUND));
        feed.checkDelete(user);
        feedRepository.delete(feed);
    }

    public FeedRes getFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new BusinessException(FeedErrorCode.FEED_NOT_FOUND));
        int likeCount = (int) feedLikeRepository.countByFeed(feed);
        return new FeedRes(feed, likeCount);
    }

    public Page<FeedListRes> getFeeds(Pageable pageable, Long userId) {
        return feedRepository.findAll(pageable)
                .map(feed -> {
                    int likeCount = (int) feedLikeRepository.countByFeed(feed);
                    boolean isLiked = false;
                    if (userId != null) {
                        User user = userRepository.findById(userId).orElse(null);
                        isLiked = user != null && feedLikeRepository.existsByUserAndFeed(user, feed);
                    }
                    return new FeedListRes(feed, likeCount, isLiked);
                });
    }

    public Feed findByFeedId(Long id) {
        return feedRepository.findById(id).orElseThrow(() -> new BusinessException(FeedErrorCode.FEED_NOT_FOUND));
    }

    public long count() {
        return feedRepository.count();
    }
}
