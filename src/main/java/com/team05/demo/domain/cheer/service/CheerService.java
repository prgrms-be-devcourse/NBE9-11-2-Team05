package com.team05.demo.domain.cheer.service;

import com.team05.demo.domain.cheer.repository.CheerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheerService {

    private final CheerRepository cheerRepository;

    public void getTodaysStatus(int userId, String timezone) {

    }


}