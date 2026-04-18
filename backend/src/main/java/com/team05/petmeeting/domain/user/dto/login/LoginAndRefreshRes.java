package com.team05.petmeeting.domain.user.dto.login;

public record LoginAndRefreshRes(

        String tokenType,
        String accessToken
) {
}
