package com.team05.demo.global.initData;

import com.team05.demo.domain.feed.repository.FeedRepository;
import com.team05.demo.domain.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    @Autowired
    @Lazy
    private BaseInitData self;
    private final FeedRepository feedRepository;
    private final FeedService feedService;

    public ApplicationRunner initData(){
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1(){
        if (feedService.count() > 0) {
            return;
        }
    }
}
