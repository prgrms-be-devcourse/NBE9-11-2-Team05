package com.team05.petmeeting.domain.naming.service;

import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.errorCode.AnimalErrorCode;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import com.team05.petmeeting.domain.naming.dto.NameProposalRes;
import com.team05.petmeeting.domain.naming.entity.AnimalNameCandidate;
import com.team05.petmeeting.domain.naming.entity.NameVoteHistory;
import com.team05.petmeeting.domain.naming.errorCode.NamingErrorCode;
import com.team05.petmeeting.domain.naming.repository.AnimalNameCandidateRepository;
import com.team05.petmeeting.domain.naming.repository.NameVoteHistoryRepository;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.errorCode.UserErrorCode;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NamingService {

    private final AnimalNameCandidateRepository candidateRepository;
    private final NameVoteHistoryRepository voteHistoryRepository;
    private final BadWordService badWordService; // Redis 기반 금칙어 검증 서비스
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;

    public NameProposalRes proposeName(Long animalId, Long userId, String proposedName) {
        // 금칙어 검증
        if(badWordService.isBadWord(proposedName)){
            throw new BusinessException(NamingErrorCode.BAD_WORD_INCLUDED);
        }

        // 해당 동물의 이름 후보 중 동일한 이름이 있는지 확인
        Optional<AnimalNameCandidate> existingCandidate =
                candidateRepository.findByAnimalIdAndProposedName(animalId, proposedName);

        if (existingCandidate.isPresent()) {
            // 이미 존재하는 이름 -> 투표로직 리다이렉트
            Long candidateId = existingCandidate.get().getId();
            vote(candidateId, userId); // 내부 투표 로직 호출
            return new NameProposalRes(candidateId, proposedName);
        }

        // 신규 제안 등록
        User proposer = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        Animal animal = animalRepository.findById(animalId).orElseThrow(
                () -> new BusinessException(AnimalErrorCode.ANIMAL_NOT_FOUND));

        AnimalNameCandidate newCandidate = new AnimalNameCandidate(animal, proposer, proposedName);
        AnimalNameCandidate savedCandidate = candidateRepository.save(newCandidate);

        // 4. 제안자 본인의 첫 투표 처리
        vote(savedCandidate.getId(), userId);

        return new NameProposalRes(savedCandidate.getId(), proposedName);
    }

    public void vote(Long candidateId, Long userId) {
        AnimalNameCandidate candidate = candidateRepository.findById(candidateId).orElseThrow(
                () -> new BusinessException(NamingErrorCode.CANDIDATE_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 중복 투표 방지 -> 한 유저는 한 동물당 한번만 투표 가능
        if (voteHistoryRepository.existsByUserIdAndAnimalId(userId, candidate.getAnimal().getId())) {
            throw new BusinessException(NamingErrorCode.ALREADY_VOTED);
        }

        // 투표 이력 저장
        NameVoteHistory history = new NameVoteHistory(user, candidate.getAnimal(), candidate);
        voteHistoryRepository.save(history);

        // 후보 테이블의 투표수 증가
        candidate.addVoteCount();
    }
}
