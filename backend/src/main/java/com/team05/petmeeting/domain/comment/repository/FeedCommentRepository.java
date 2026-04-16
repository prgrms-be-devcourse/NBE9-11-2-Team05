package com.team05.petmeeting.domain.comment.repository;

import com.team05.petmeeting.domain.comment.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    List<FeedComment> findByFeed_Id(Long feedId);
}