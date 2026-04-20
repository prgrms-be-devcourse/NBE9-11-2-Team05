package com.team05.petmeeting.domain.adoption.controller;

import com.team05.petmeeting.domain.adoption.dto.request.AdoptionApplyRequest;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.dto.response.AdoptionDetailResponse;
import com.team05.petmeeting.domain.adoption.service.AdoptionService;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;

    public List<AdoptionApplyResponse> getMyadoptions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return adoptionService.getMyadoptions(userId);
    }

    @GetMapping("/{applicationId}")
    public AdoptionDetailResponse getApplicationDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId
    ) {
        return adoptionService.getApplicationDetail(userDetails.getUserId(), applicationId);
    }

    @PostMapping("/adoptions/{animalId}")
    public AdoptionApplyResponse applyApplication(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long animalId,
            @RequestBody AdoptionApplyRequest request){

        return adoptionService.applyApplication(userDetails.getUserId(),animalId, request);

    }

}
