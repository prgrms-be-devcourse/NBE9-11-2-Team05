package com.team05.demo.domain.user.dto.signup;

public record SignupResponse(
        Long userId,
        String username,
        String nickname
) {
}
