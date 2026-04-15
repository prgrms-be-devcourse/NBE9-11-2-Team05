package com.team05.demo.domain.user.dto.login;

public record LoginResponse(

        String tokenType,
        String accessToken
) {
}
