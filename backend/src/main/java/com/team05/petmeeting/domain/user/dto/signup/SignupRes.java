package com.team05.petmeeting.domain.user.dto.signup;

public record SignupRes(
        Long userId,
        String username,
        String nickname
) {
}
