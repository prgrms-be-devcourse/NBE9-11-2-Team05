package com.team05.petmeeting.domain.adoption.controller;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionDetailResponse;
import com.team05.petmeeting.domain.adoption.service.AdoptionService;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;

    public List<AdoptionApplyResponse> getMyadoptions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return adoptionService.getMyadoptions(userDetails.getUserId());
    }

    @GetMapping("/{applicationId}")
    public AdoptionDetailResponse getApplicationDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId
    ) {
        return adoptionService.getApplicationDetail(userDetails.getUserId(), applicationId);
    }

}
