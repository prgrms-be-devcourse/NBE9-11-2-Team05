package com.team05.demo.domain.comment.entity;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comments")
public class Comment extends BaseEntity {

    // 유기동물에 달린 댓글일 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = true)
    private Feed feed;

    // 피드글에 달린 댓글일 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desertion_no", nullable = true)
    private Animal animal;

    @Column(length = 255, nullable = false)
    private String content;

}