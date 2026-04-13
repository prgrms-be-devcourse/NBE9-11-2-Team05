package com.team05.demo.domain.comment.entity;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.feed.entity.Feed;
import com.team05.demo.global.entity.BaseEntity;
import com.team05.demo.global.exception.BusinessException;
import com.team05.demo.global.exception.GlobalErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    // 피드글에 달린 댓글일 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = true)
    private Feed feed;

    // 유기동물에 달린 댓글일 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = true)
    private Animal animal;

    @Column(length = 255, nullable = false)
    private String content;

    // animal/feed 둘 다 있거나 둘 다 없는 상황 방지
    @PrePersist
    @PreUpdate
    private void validateCommentTarget() {
        boolean hasFeed = (this.feed != null);
        boolean hasAnimal = (this.animal != null);

        if (hasFeed == hasAnimal) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
        }
    }

    @Builder(access = AccessLevel.PRIVATE) // 내부에서만 사용할 생성자 (Builder)
    private Comment (Feed feed, Animal animal, String content) {
        this.feed = feed;
        this.animal = animal;
        this.content = content;
    }

    public static Comment createAnimalComment(Animal animal, String content){
        return Comment.builder()
                .animal(animal)
                .content(content)
                .build();
    }

    public static Comment createFeedComment(Feed feed, String content){
        return Comment.builder()
                .feed(feed)
                .content(content)
                .build();
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

}