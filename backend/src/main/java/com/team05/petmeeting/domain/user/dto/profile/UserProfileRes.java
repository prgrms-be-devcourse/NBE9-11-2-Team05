package com.team05.petmeeting.domain.user.dto.profile;

import com.team05.petmeeting.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserProfileRes(
        String username,
        String profileImageUrl,
        String nickname,
        String name,
        LocalDateTime createdAt
) {
    public static UserProfileRes from(User user) {
        return new UserProfileRes(user.getUsername(), user.getProfileImageUrl(), user.getNickname(), user.getRealname(), user.getCreatedAt());
    }
}
