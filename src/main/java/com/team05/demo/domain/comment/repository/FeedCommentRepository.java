package com.team05.demo.domain.comment.repository;

import com.team05.demo.domain.comment.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
}