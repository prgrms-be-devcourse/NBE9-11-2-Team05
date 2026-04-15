package com.team05.petmeeting.domain.comment.entity;

import com.team05.petmeeting.domain.feed.entity.Feed;
import com.team05.petmeeting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "feed_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(length = 255, nullable = false)
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private FeedComment(Feed feed, String content) {
        this.feed = feed;
        this.content = content;
    }

    public static FeedComment create(Feed feed, String content) {
        FeedComment comment = FeedComment.builder()
                .feed(feed)
                .content(content)
                .build();

        feed.getComments().add(comment);

        return comment;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}