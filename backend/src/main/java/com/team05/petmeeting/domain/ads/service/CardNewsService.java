package com.team05.petmeeting.domain.ads.service;

import com.team05.petmeeting.domain.animal.entity.Animal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
public class CardNewsService {
    //Claude API로 카드뉴스 텍스트 생성
    @Value("${claude.api.key}")
    private String apiKey;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-20250514";

    public String generateCardNews(Animal animal) {
        String prompt = """
                다음 유기동물 정보를 바탕으로 SNS 광고용 카드뉴스 텍스트를 작성해줘.
                
                동물 정보:
                - 종류: %s
                - 품종: %s
                - 나이: %s
                - 성별: %s
                - 색상: %s
                - 특징: %s
                - 보호소: %s
                
                조건:
                - 200자 이내
                - 감성적이고 따뜻한 톤
                - 입양을 유도하는 문구 포함
                - 해시태그 3개 포함
                """.formatted(
                animal.getUpKindNm(),
                animal.getKindFullNm(),
                animal.getAge(),
                animal.getSexCd(),
                animal.getColorCd(),
                animal.getSpecialMark() != null ? animal.getSpecialMark() : "없음",
                animal.getCareNm()
        );

        String requestBody = """
                {
                    "model": "%s",
                    "max_tokens": 1024,
                    "messages": [
                        {"role": "user", "content": "%s"}
                    ]
                }
                """.formatted(MODEL, prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        try {
            return RestClient.create()
                    .post()
                    .uri(CLAUDE_API_URL)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw new IllegalStateException(
                    "Claude API 호출 실패: %s".formatted(e.getResponseBodyAsString()), e
            );
        }
    }
}