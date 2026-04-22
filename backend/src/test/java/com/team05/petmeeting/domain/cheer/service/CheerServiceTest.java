package com.team05.petmeeting.domain.cheer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheerService 단위 테스트")
class CheerServiceTest {

//    @Mock
//    private CheerRepository cheerRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private AnimalRepository animalRepository;
//
//    @InjectMocks
//    private CheerService cheerService;
//
//    private User testUser;
//    private Animal testAnimal;
//
//    @BeforeEach
//    void setUp() {
//        testUser = createUser(1L, 0, LocalDate.now());
//        testAnimal = createAnimal(1L, 0);
//    }
//
//    private User createUser(Long id, int heartCount, LocalDate resetDate) {
//        User user = new User(
//                "testUser",
//                "password123",
//                "테스트유저",
//                "USER",
//                heartCount,
//                resetDate
//        );
//        ReflectionTestUtils.setField(user, "id", id);
//        return user;
//    }
//
//    private Animal createAnimal(Long id, int totalCheerCount) {
//        Animal animal = new Animal(
//                "D123456",
//                "보호중",
//                "N123",
//                LocalDate.of(2024, 8, 10),
//                "개",
//                "믹스견",
//                "갈색",
//                "2020(년생)",
//                "5kg",
//                "M",
//                "img1.jpg",
//                "img2.jpg",
//                "테스트 보호소",
//                "010-1234-5678",
//                totalCheerCount
//        );
//        ReflectionTestUtils.setField(animal, "id", id);
//        return animal;
//    }
//
//    @Test
//    @DisplayName("getTodaysStatus: 정상 조회")
//    void getTodaysStatus_success() {
//        testUser.useDailyCheer();
//        testUser.useDailyCheer();
//        testUser.useDailyCheer();
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//
//        CheerStatusDto result = cheerService.getTodaysStatus(1L);
//
//        assertThat(result.usedToday()).isEqualTo(3);
//        assertThat(result.remainingToday()).isEqualTo(2);
//        assertThat(result.resetAt()).isEqualTo(LocalDate.now().plusDays(1).atStartOfDay().toString());
//    }
//
//    @Test
//    @DisplayName("getTodaysStatus: 사용자 없음")
//    void getTodaysStatus_userNotFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> cheerService.getTodaysStatus(1L))
//                .isInstanceOf(BusinessException.class)
//                .extracting("errorCode")
//                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
//    }
//
//    @Test
//    @DisplayName("getTodaysStatus: 마지막 초기화일이 오늘이 아니면 응원 횟수를 초기화한다")
//    void getTodaysStatus_resetWhenNeeded() {
//        testUser = createUser(1L, 5, LocalDate.now().minusDays(1));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//
//        CheerStatusDto result = cheerService.getTodaysStatus(1L);
//
//        assertThat(result.usedToday()).isZero();
//        assertThat(result.remainingToday()).isEqualTo(5);
//        assertThat(testUser.getLastHeartResetDate()).isEqualTo(LocalDate.now());
//    }
//
//    @Test
//    @DisplayName("cheerAnimal: 정상 동작")
//    void cheerAnimal_success() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
//        when(cheerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        CheerRes result = cheerService.cheerAnimal(1L, 1L);
//
//        assertThat(result.animalId()).isEqualTo(1L);
//        assertThat(result.cheerCount()).isEqualTo(0);
//        assertThat(result.temperature()).isEqualTo(0.0);
//        assertThat(result.remaingCheersToday()).isEqualTo(4);
//        assertThat(testUser.getDailyHeartCount()).isEqualTo(1);
//
//        verify(cheerRepository).save(any());
//        verify(animalRepository).incrementCheerCount(1L);
//    }
//
//    @Test
//    @DisplayName("cheerAnimal: 사용자 없음")
//    void cheerAnimal_userNotFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> cheerService.cheerAnimal(1L, 1L))
//                .isInstanceOf(BusinessException.class)
//                .extracting("errorCode")
//                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
//    }
//
//    @Test
//    @DisplayName("cheerAnimal: 동물 없음")
//    void cheerAnimal_animalNotFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(animalRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> cheerService.cheerAnimal(1L, 1L))
//                .isInstanceOf(BusinessException.class)
//                .extracting("errorCode")
//                .isEqualTo(AnimalErrorCode.ANIMAL_NOT_FOUND);
//    }
//
//    @Test
//    @DisplayName("cheerAnimal: 일일 응원 제한 초과")
//    void cheerAnimal_limitExceeded() {
//        testUser = createUser(1L, 5, LocalDate.now());
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
//
//        assertThatThrownBy(() -> cheerService.cheerAnimal(1L, 1L))
//                .isInstanceOf(BusinessException.class)
//                .extracting("errorCode")
//                .isEqualTo(CheerErrorCode.DAILY_CHEER_LIMIT_EXCEEDED);
//
//        verify(cheerRepository, never()).save(any());
//        verify(animalRepository, never()).incrementCheerCount(any());
//    }
//
//    @Test
//    @DisplayName("cheerAnimal: 4회 사용 상태면 마지막 1회를 사용할 수 있다")
//    void cheerAnimal_boundaryAtFourCheers() {
//        testUser = createUser(1L, 4, LocalDate.now());
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
//        when(cheerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        CheerRes result = cheerService.cheerAnimal(1L, 1L);
//
//        assertThat(result.remaingCheersToday()).isZero();
//        assertThat(testUser.getDailyHeartCount()).isEqualTo(5);
//    }
//
//    @Test
//    @DisplayName("cheerAnimal: 전날 사용량은 응원 전에 초기화된다")
//    void cheerAnimal_resetsDailyCountBeforeUse() {
//        testUser = createUser(1L, 5, LocalDate.now().minusDays(1));
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
//        when(cheerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        CheerRes result = cheerService.cheerAnimal(1L, 1L);
//
//        assertThat(result.remaingCheersToday()).isEqualTo(4);
//        assertThat(testUser.getDailyHeartCount()).isEqualTo(1);
//        assertThat(testUser.getLastHeartResetDate()).isEqualTo(LocalDate.now());
//    }
//
//    @Test
//    @DisplayName("cheerAnimal: Cheer 객체를 사용자와 동물로 생성해 저장한다")
//    void cheerAnimal_savesCheerWithUserAndAnimal() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
//        when(cheerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        cheerService.cheerAnimal(1L, 1L);
//
//        verify(cheerRepository).save(argThat(cheer ->
//                cheer.getUser() == testUser &&
//                        cheer.getAnimal() == testAnimal
//        ));
//    }
}

