package com.team05.petmeeting.domain.ads.service;

import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdsService {
    //Top N 선정 + 전체 파이프라인 연결

    private final AnimalRepository animalRepository;
    private final CardNewsService cardNewsService;

    // Top N 동물 조회
    public List<Animal> getTopAnimals(int n) {
        return animalRepository.findAllByOrderByTotalCheerCountDesc(
                PageRequest.of(0, n)
        );
    }
}
