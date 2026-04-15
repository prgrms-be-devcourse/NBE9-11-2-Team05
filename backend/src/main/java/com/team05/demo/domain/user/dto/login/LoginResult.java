package com.team05.demo.domain.user.dto.login;

public record LoginResult(
        String refreshToken,
        LoginResponse loginResponse
) {
}
