package com.team05.demo.domain.user.dto.login;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "id는 필수 입력값입니다.")
        String username,

        @NotBlank(message = "password는 필수 입력값입니다.")
        String password
) {
}
