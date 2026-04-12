package com.team05.demo.domain.cheer.controller;

import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.domain.cheer.dto.AnimalCheersDto;
import com.team05.demo.domain.cheer.dto.CheerRes;
import com.team05.demo.domain.cheer.dto.CheerStatusDto;
import com.team05.demo.domain.cheer.service.CheerService;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CheerController", description = "응원 API")
@RequiredArgsConstructor
public class CheerController {

    private final AnimalRepository animalRepository; // todo: AnimalService 정의 후 교체 예정(예외처리도 서비스에서 진행될 예정)
    private final CheerService cheerService;


    /**
     * GET /api/v1/cheers/today
     * 사용자의 오늘 응원 상태 조회
     * @param user 인증된 사용자 (JWT 토큰으로부터)
     * @return RsData<CheerStatusDto>
     */
    @GetMapping("/cheers/today")
    @Operation(summary = "잔여 응원 횟수 조회")
    public RsData<CheerStatusDto> getTodaysCheers(
            @AuthenticationPrincipal User user // todo: 검증된 user 가져오기
    ) {
        // Service 호출
        CheerStatusDto status = cheerService.getTodaysStatus(user.getId());
        return new RsData<>(
                "오늘의 잔여 응원 횟수 조회 성공",
                "200",
                status
        );
    }

    /**
     * GET /api/v1/animals/{animalId}/cheers
     * 특정 동물의 응원 수 및 온도 조회
     * (인증 불필요)
     * @param animalId 동물 ID
     * @return RsData<AnimalCheersDto>
     */
    @GetMapping("/animals/{animalId}/cheers")
    @Operation(summary = "응원 수 및 온도 조회")
    public RsData<AnimalCheersDto> getAnimalCheers(
            @PathVariable long animalId
    ) {
        AnimalCheersDto animalCheers = cheerService.getAnimalCheers(animalId);

        return new RsData<>(
                "동물의 응원 정보 조회 성공",
                "200",
                animalCheers
        );
    }

    /**
     * POST /api/v1/animals/{animalId}/cheers
     * 특정 동물에 응원 부여
     * @param animalId 응원할 동물 ID
     * @param user 인증된 사용자 (JWT 토큰으로부터)
     * @return RsData<CheerRes>
     */
    @PostMapping("/animals/{animalId}/cheers")
    @Operation(summary = "응원 부여")
    public RsData<CheerRes> cheerAnimal(
            @PathVariable Long animalId,
            @AuthenticationPrincipal User user
    ) {
        CheerRes cheerRes = cheerService.cheerAnimal(animalId, user.getId());

        return new RsData<>(
                "응원 부여 성공!",
                "201",
                cheerRes
        );
    }




}
