package com.team05.demo.domain.cheer.service;

import com.team05.demo.domain.animal.entity.Animal;
import com.team05.demo.domain.animal.repository.AnimalRepository;
import com.team05.demo.domain.cheer.dto.AnimalCheersDto;
import com.team05.demo.domain.cheer.dto.CheerRes;
import com.team05.demo.domain.cheer.dto.CheerStatusDto;
import com.team05.demo.domain.cheer.entity.Cheer;
import com.team05.demo.domain.cheer.repository.CheerRepository;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.domain.user.repository.UserRepository;
import com.team05.demo.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CheerService {

    private final CheerRepository cheerRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;

    // 오늘 응원 상태 조회
    public CheerStatusDto getTodaysStatus(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ServiceException("400", "존재하지 않는 사용자")
        );

        int usedToday = user.getDailyHeartCount();
        int remainingToday = 5 - usedToday;

        // 내일 자정 계산 (DB에 저장하지 않고, 매번 계산해서 사용)
        LocalDateTime tomorrow_midnight = LocalDate.now().plusDays(1) // 2026-04-13
                                    .atStartOfDay();       // 00:00:00
        String resetAt = tomorrow_midnight.toString();

        return new CheerStatusDto(usedToday, remainingToday, resetAt);
    }

    // 동물 응원 정보 조회
    public AnimalCheersDto getAnimalCheers(long animalId) {
        Animal animal = animalRepository.findById(animalId).orElseThrow(
                () -> new ServiceException("400", "존재하지 않는 동물")
        );

        int cheerCount = animal.getTotalCheerCount();
        double temperature = calculateTemperature(cheerCount, 50);

        return new AnimalCheersDto(animalId, cheerCount, temperature);
    }

    // 응원 부여
    public CheerRes cheerAnimal(long userId, long animalId) {
        // 사용자 조회
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ServiceException("400", "존재하지 않는 사용자")
        );
        // 동물 조회
        Animal animal = animalRepository.findById(animalId).orElseThrow(
                () -> new ServiceException("400", "존재하지 않는 동물")
        );
        // 5회 제한 확인
        if (user.getDailyHeartCount() >= 5) {
            throw new ServiceException("429", "오늘의 응원 하트를 모두 사용했습니다. 자정에 초기화됩니다.");
        }
        // cheer 객체 생성 & 저장
        Cheer cheer = new Cheer(user, animal);
        cheerRepository.save(cheer);
        // user 응원 횟수 증가
        user.useDailyCheer();
        animal.increaseCheerCount(); // todo: 동시성제어 처리
        // 응원 부여 후 동물의 최신 정보 조회
        int newCheerCount = animal.getTotalCheerCount();
        double newTemperature = calculateTemperature(newCheerCount, 50);
        int remainingHeartsToday = 5 - user.getDailyHeartCount();

        return new CheerRes(
                animalId,
                newCheerCount,
                newTemperature,
                remainingHeartsToday
        );
    }

    // temperature = (heart_count / 목표_하트수) × 100 | 목표기본값 = 50
    private double calculateTemperature(int cheersCount, int goalCount) {
        return (double) cheersCount / goalCount;
    }


}