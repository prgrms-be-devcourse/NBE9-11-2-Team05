package com.team05.demo.domain.feed.entity;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.feed.enums.FeedCategory;
import com.team05.demo.domain.comment.entity.Comment;
import com.team05.demo.domain.feed.errorCode.FeedErrorCode;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.global.entity.BaseEntity;
import com.team05.demo.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feeds")
public class Feed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true) //임시!
    private User user;    // FK

    @ManyToOne(fetch = FetchType.LAZY)
    private Animal animal; // FK

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedCategory category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    public Feed(User user, FeedCategory category, String title, String content, String imageUrl){
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
    public void update(FeedCategory category, String title, String content, String imageUrl){
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
    public void checkModify(User user){
//        JWT 구현 후 권한 검증 추가
    }

    public void checkDelete(User user){
//        JWT 구현 후 권한 검증 추가
//        if(!this.user.getId().equals(user.getId())) {
//            throw new BusinessException(FeedErrorCode.FORBIDDEN);
//        } 임시
    }

    @OneToMany (mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

}
