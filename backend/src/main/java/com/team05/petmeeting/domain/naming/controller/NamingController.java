package com.team05.petmeeting.domain.naming.controller;

import com.team05.petmeeting.domain.naming.repository.AnimalNameCandidateRepository;
import com.team05.petmeeting.domain.naming.service.NamingService;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/naming")
@Tag(name = "NamingController", description = "동물이름 투표(작명) API")
public class NamingController { // 제안/투표/후보조회 API

    private final AnimalNameCandidateRepository candidateRepository;
    private final NamingService namingService;

    @GetMapping("/animals/{animalId}/candidates")
    @Operation(summary = "이름 후보 조회", description = "득표순 내림차순 조회")
    public void getNameCandidates(
            @PathVariable Long animalId
    ) {

    }

    @PostMapping("/animals/{animalId}/propose")
    @Operation(summary = "이름 작명(제안)")
    public void proposeName(
            @PathVariable Long animalId,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

    }

    @PostMapping("/candidates/{candidateId}/vote")
    @Operation(summary = "기존 이름 투표")
    public void voteName(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

    }

    @PatchMapping("/candidates/{candidateId}/confirm")
    @Operation(summary = "관리자가 이름 확정", description = "해당 동물의 보호소 관리자가 최종 확정 실시")
    public void confirmNameByAdmin(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

    }

    @GetMapping("/admin/badwords")
    @Operation(summary = "금칙어 조회")
    public void getBadWords() {

    }

    @PostMapping("/admin/badwords")
    @Operation(summary = "금칙어 추가")
    public void addBadWord(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

    }

    @DeleteMapping("/admin/badwords/{badwordId}")
    @Operation(summary = "금칙어 삭제")
    public void deleteBadWord(
            @PathVariable Long badwordId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

    }




}
