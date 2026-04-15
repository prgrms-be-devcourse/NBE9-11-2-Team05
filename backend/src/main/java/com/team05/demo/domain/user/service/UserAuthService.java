package com.team05.demo.domain.user.service;

import com.team05.demo.domain.user.dto.signup.SignupRequest;
import com.team05.demo.domain.user.dto.signup.SignupResponse;
import com.team05.demo.domain.user.entity.User;
import com.team05.demo.domain.user.errorCode.UserErrorCode;
import com.team05.demo.domain.user.repository.UserRepository;
import com.team05.demo.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        // username 중복 체크
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 엔티티 생성
        User user = User.create(
                request.username(),
                encodedPassword,
                request.nickname(),
                request.realname()
        );

        // 저장
        User savedUser = userRepository.save(user);

        // 응답 생성
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getNickname()
        );
    }

}