package com.team05.petmeeting.domain.user.dto.login;

public record LoginAndRefreshResponse(

        String tokenType,
        String accessToken
) {
}
