package com.team05.petmeeting.domain.comment.repository;

import com.team05.petmeeting.domain.comment.entity.FeedComment;
import com.team05.petmeeting.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    List<FeedComment> findByFeed(Feed feed);
}