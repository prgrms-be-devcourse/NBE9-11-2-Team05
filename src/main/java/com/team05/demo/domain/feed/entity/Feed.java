package com.team05.demo.domain.feed.entity;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feeds")
public class Feed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;    // FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desertion_no")
    private Animal animal; // FK

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

}
