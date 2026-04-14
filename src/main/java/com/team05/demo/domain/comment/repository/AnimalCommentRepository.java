package com.team05.demo.domain.comment.repository;

import com.team05.demo.domain.comment.entity.AnimalComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalCommentRepository extends JpaRepository<AnimalComment, Long> {
}
