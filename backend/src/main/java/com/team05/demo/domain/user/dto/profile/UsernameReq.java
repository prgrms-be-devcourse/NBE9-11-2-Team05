package com.team05.demo.domain.user.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsernameReq (
        @NotBlank(message = "id는 필수 입력값입니다.")
        @Size(min = 5, max = 20, message = "id는 5~20자 사이여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]{5,20}$",
                message = "id는 영문, 숫자, 특수문자만 사용할 수 있습니다."
        )
        String newUsername
){
}
