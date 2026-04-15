package com.team05.petmeeting.domain.user.dto.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank(message = "id는 필수 입력값입니다.")
        @Size(min = 5, max = 20, message = "id는 5~20자 사이여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]{5,20}$",
                message = "id는 영문, 숫자, 특수문자만 사용할 수 있습니다."
        )
        String username,

        @NotBlank(message = "password는 필수 입력값입니다.")
        @Size(min = 8, max = 16, message = "password는 8~16자 사이여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).+$",
                message = "password는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "nickname은 필수 입력값입니다.")
        String nickname,

        @NotBlank(message = "realname은 필수 입력값입니다.")
        String realname
) {
}
