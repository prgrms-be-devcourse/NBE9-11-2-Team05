package com.team05.petmeeting.domain.user.dto.login;

public record LoginResponse(

        String tokenType,
        String accessToken
) {
}
