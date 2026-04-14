package com.team05.demo.domain.cheer.controller;

import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.domain.cheer.dto.CheerRes;
import com.team05.demo.domain.cheer.dto.CheerStatusDto;
import com.team05.demo.domain.cheer.service.CheerService;
import com.team05.demo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CheerController", description = "응원 API")
@RequiredArgsConstructor
public class CheerController {

    private final AnimalRepository animalRepository; // todo: AnimalService 정의 후 교체 예정(예외처리도 서비스에서 진행될 예정)
    private final CheerService cheerService;


    @GetMapping("/cheers/today")
    @Operation(summary = "잔여 응원 횟수 조회")
    public ResponseEntity<CheerStatusDto> getTodaysCheers(
            @AuthenticationPrincipal User user // todo: 검증된 user 가져오기
    ) {
        // Service 호출
        CheerStatusDto status = cheerService.getTodaysStatus(user.getId());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/animals/{animalId}/cheers")
    @Operation(summary = "응원 부여")
    public ResponseEntity<CheerRes> cheerAnimal(
            @PathVariable Long animalId,
            @AuthenticationPrincipal User user
    ) {
        CheerRes cheerRes = cheerService.cheerAnimal(animalId, user.getId());

        return ResponseEntity.ok(cheerRes);
    }




}
