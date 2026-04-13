package com.team05.demo.domain.feed.entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.feed.dto.FeedRequest;
import com.team05.demo.domain.feed.enums.FeedCategory;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
