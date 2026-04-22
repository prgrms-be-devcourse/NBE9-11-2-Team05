package com.team05.petmeeting.domain.adoption.controller;

import com.team05.petmeeting.domain.adoption.dto.response.AdoptionApplyResponse;
import com.team05.petmeeting.domain.adoption.service.AdoptionAdminService;
import com.team05.petmeeting.global.security.userdetails.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adoptions/admin")
@RequiredArgsConstructor
public class AdoptionAdminController {

    private final AdoptionAdminService adoptionAdminService;

    @GetMapping
    public List<AdoptionApplyResponse> getManagedShelterApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return adoptionAdminService.getManagedShelterApplications(userDetails.getUserId());
    }
}
