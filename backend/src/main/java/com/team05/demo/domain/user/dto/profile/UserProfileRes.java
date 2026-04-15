package com.team05.demo.domain.user.dto.profile;

import com.team05.demo.domain.user.entity.User;

public record UserProfileRes (
        String username,
        String profileImageUrl,
        String nickname
){
    public static UserProfileRes from(User user) {
        return new UserProfileRes(user.getUsername(), user.getProfileImageUrl(), user.getNickname());
    }
}
