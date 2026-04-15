package com.team05.demo.domain.feed.repository;

import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Long countByUser(User user);

    List<Feed> findAllByUserOrderByCreatedAtDesc(User user);
}
