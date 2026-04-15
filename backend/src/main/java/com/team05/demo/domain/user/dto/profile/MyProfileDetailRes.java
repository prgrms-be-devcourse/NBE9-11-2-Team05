package com.team05.demo.domain.user.dto.profile;

public record MyProfileDetailRes (
        Long feedCount,
        Long cheerCount
){
    public static MyProfileDetailRes from(Long feedCount, Long cheerCount) {
        return new MyProfileDetailRes(feedCount, cheerCount);
    }
}
