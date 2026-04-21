package com.team05.petmeeting.naming.service;

import com.team05.petmeeting.domain.animal.dto.external.AnimalItem;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.repository.AnimalRepository;
import com.team05.petmeeting.domain.naming.dto.NameProposalRes;
import com.team05.petmeeting.domain.naming.entity.AnimalNameCandidate;
import com.team05.petmeeting.domain.naming.entity.NameVoteHistory;
import com.team05.petmeeting.domain.naming.errorCode.NamingErrorCode;
import com.team05.petmeeting.domain.naming.repository.AnimalNameCandidateRepository;
import com.team05.petmeeting.domain.naming.repository.NameVoteHistoryRepository;
import com.team05.petmeeting.domain.naming.service.BadWordService;
import com.team05.petmeeting.domain.naming.service.NamingService;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NamingServiceTest {

    @InjectMocks
    private NamingService namingService;

    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private AnimalNameCandidateRepository candidateRepository;
    @Mock
    private NameVoteHistoryRepository voteHistoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BadWordService badWordService;

    private User user;
    private Animal animal;

    @BeforeEach
    void setUp() {
        // 테스트용 기본 객체 생성 (Reflection을 사용해 ID 강제 주입)
        user = User.create("test@test.com", "테스터", "홍길동");
        ReflectionTestUtils.setField(user, "id", 1L);

        // Animal 객체 생성 및 상태 설정
        animal = Animal.from(new AnimalItem()); // 필요한 필드만 채워진 item 가정
        ReflectionTestUtils.setField(animal, "id", 100L);
        ReflectionTestUtils.setField(animal, "stateGroup", 0); // 보호중
    }

    @Test
    @DisplayName("이름 제안 성공 - 금칙어가 없고 신규 이름일 경우")
    void proposeName_Success() {
        // given
        String proposedName = "초코";
        given(animalRepository.findById(100L)).willReturn(Optional.of(animal));
        given(badWordService.isBadWord(proposedName)).willReturn(false);
        given(candidateRepository.findByAnimalIdAndProposedName(100L, proposedName)).willReturn(Optional.empty());

        // 이 부분이 핵심입니다: vote() 메서드 내부에서 호출되는 userRepository 조회 대응
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        AnimalNameCandidate newCandidate = new AnimalNameCandidate(animal, user, proposedName);
        ReflectionTestUtils.setField(newCandidate, "id", 1L);

        // candidateId를 통해 다시 후보를 찾는 로직 대응 (vote 메서드 내부)
        given(candidateRepository.findById(1L)).willReturn(Optional.of(newCandidate));
        given(candidateRepository.save(any(AnimalNameCandidate.class))).willReturn(newCandidate);

        // when
        NameProposalRes response = namingService.proposeName(100L, 1L, proposedName);

        // then
        assertThat(response.proposedName()).isEqualTo(proposedName);
        verify(candidateRepository, times(1)).save(any(AnimalNameCandidate.class));
        verify(voteHistoryRepository, times(1)).save(any(NameVoteHistory.class));
    }

    @Test
    @DisplayName("중복 투표 방지 - 이미 투표한 유저가 다시 투표 시도 시 예외 발생")
    void vote_Fail_AlreadyVoted() {
        // given
        Long candidateId = 1L;
        AnimalNameCandidate candidate = new AnimalNameCandidate(animal, user, "바둑이");
        given(candidateRepository.findById(candidateId)).willReturn(Optional.of(candidate));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // 이미 투표 이력이 있다고 가정
        given(voteHistoryRepository.existsByUserIdAndAnimalId(1L, 100L)).willReturn(true);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            namingService.vote(candidateId, 1L);
        });

        assertThat(exception.getErrorCode()).isEqualTo(NamingErrorCode.ALREADY_VOTED);
    }

    @Test
    @DisplayName("관리자 이름 확정 - Animal 엔티티에 이름이 정상 반영되는지 확인")
    void confirmName_Success() {
        // given
        Long candidateId = 1L;
        AnimalNameCandidate candidate = new AnimalNameCandidate(animal, user, "복실이");
        given(candidateRepository.findById(candidateId)).willReturn(Optional.of(candidate));

        // when
        namingService.confirmName(candidateId, 999L); // managerId

        // then
        assertThat(candidate.isConfirmed()).isTrue();
        assertThat(animal.getName()).isEqualTo("복실이");
    }
}
