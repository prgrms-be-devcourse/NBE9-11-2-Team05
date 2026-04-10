package com.team05.demo.domain.feed.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Feed {
    @Id
    private Long id;

}
