package com.team05.petmeeting.domain.user.dto.signup;

public record SignupResponse(
        Long userId,
        String username,
        String nickname
) {
}
