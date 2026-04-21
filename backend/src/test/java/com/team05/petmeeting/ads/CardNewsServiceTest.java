package com.team05.petmeeting.ads;

import com.team05.petmeeting.domain.ads.dto.CardNewsResult;
import com.team05.petmeeting.domain.ads.service.CardNewsService;
import com.team05.petmeeting.domain.ads.service.GeminiService;
import com.team05.petmeeting.domain.ads.service.ImageComposer;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.infra.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardNewsServiceTest {

    @Test
    @DisplayName("카드뉴스 생성 테스트 (외부 API Mock)")
    void generateCardNews() {
        // given
        Animal animal = mock(Animal.class);
        when(animal.getKindFullNm()).thenReturn("골든 리트리버");
        when(animal.getSpecialMark()).thenReturn("사람을 좋아함");
        when(animal.getPopfile1()).thenReturn("https://via.placeholder.com/300");
        when(animal.getDesertionNo()).thenReturn("123");

        GeminiService geminiService = mock(GeminiService.class);
        S3Service s3Service = mock(S3Service.class);
        ImageComposer imageComposer = mock(ImageComposer.class);  // 추가

        when(geminiService.generate(any())).thenReturn("입양해주세요\n사람을 좋아해요");
        when(imageComposer.compose(any(), any())).thenReturn(new byte[]{1, 2, 3});  // 추가: 실제 접속 차단
        when(s3Service.upload(any(), any())).thenReturn("https://s3-url.com/image.png");

        CardNewsService service = new CardNewsService(geminiService, s3Service, imageComposer);  // 변경

        // when
        CardNewsResult result = service.generateCardNews(animal);

        // then
        assertNotNull(result);
        assertEquals("https://s3-url.com/image.png", result.imageUrl());
        assertTrue(result.caption().contains("입양"));
    }
}