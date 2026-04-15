package com.team05.petmeeting.domain.user.dto.login;

public record LoginResult(
        String refreshToken,
        LoginResponse loginResponse
) {
}
