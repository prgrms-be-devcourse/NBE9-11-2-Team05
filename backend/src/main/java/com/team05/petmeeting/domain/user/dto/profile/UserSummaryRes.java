package com.team05.petmeeting.domain.user.dto.profile;

import com.team05.petmeeting.domain.user.entity.User;

public record UserSummaryRes(
        String username,
        String nickname,
        String name,
        String profileImageUrl
) {
    public static UserSummaryRes from(User user) {
        return new UserSummaryRes(
                user.getUsername(),
                user.getNickname(),
                user.getRealname(),
                user.getProfileImageUrl()
        );
    }
}
