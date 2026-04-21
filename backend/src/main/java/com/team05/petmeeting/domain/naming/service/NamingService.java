package com.team05.petmeeting.domain.naming.service;

import com.team05.petmeeting.domain.naming.dto.BadWordAddRes;
import com.team05.petmeeting.domain.naming.dto.BadWordListRes;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public class NamingService {
    public BadWordListRes getBadWords() {
        return null;
    } // 투표/확정 비즈니스 로직

    public BadWordAddRes addBadWord(@NotBlank(message = "추가할 금칙어를 입력해주세요.") String s) {
        return null;
    }

    public void deleteBadWord(Long badwordId) {

    }
}
