package com.team05.demo.domain.comment.entity;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comments")
public class Comment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    // 유기동물에 달린 댓글일 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = true)
    private Feed feed;

    // 피드글에 달린 댓글일 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="desertion_no", nullable = true)
    private Animal animal;

    @Column (length = 255, nullable = false)
    private String content;

}