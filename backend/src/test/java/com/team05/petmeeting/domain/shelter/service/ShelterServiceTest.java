package com.team05.petmeeting.domain.shelter.service;

import com.team05.petmeeting.domain.shelter.repository.ShelterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShelterServiceTest {

    @Mock
    ShelterRepository repository;

    @InjectMocks
    ShelterService service;

    @Test
    @DisplayName("보호소 생성 성공")
    void test1() {
        // given
        // when
        // then
    }
}