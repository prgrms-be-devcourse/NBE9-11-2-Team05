package com.team05.petmeeting.domain.comment.repository;

import com.team05.petmeeting.domain.comment.entity.AnimalComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalCommentRepository extends JpaRepository<AnimalComment, Long> {
}
