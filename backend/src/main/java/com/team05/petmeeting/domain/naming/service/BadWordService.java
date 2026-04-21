package com.team05.petmeeting.domain.naming.service;

import com.team05.petmeeting.domain.naming.repository.BadWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BadWordService { // Redis 기반 금칙어 검증 로직

    private final BadWordRepository badWordRepository;
    // 기존 RedisConfig에 정의된 빈 사용
    private final StringRedisTemplate stringRedisTemplate;

    private static final String BAD_WORD_KEY = "naming:badwords";

    public boolean isBadWord(String word) {
        // StringRedisTemplate은 내부적으로 StringSerializer를 사용하므로
        // 별도의 직렬화 설정 없이 바로 사용 가능하다.
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(BAD_WORD_KEY, word));
    }
}
