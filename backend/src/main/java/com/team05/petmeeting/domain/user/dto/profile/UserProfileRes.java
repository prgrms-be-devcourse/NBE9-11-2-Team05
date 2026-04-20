package com.team05.petmeeting.domain.user.dto.profile;

import com.team05.petmeeting.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserProfileRes(
        String profileImageUrl,
        String nickname,
        String realname,
        LocalDateTime createdAt
) {
    public static UserProfileRes from(User user) {
        return new UserProfileRes(
                user.getProfileImageUrl(),
                user.getNickname(),
                user.getRealname(),
                user.getCreatedAt()
        );
    }
}
