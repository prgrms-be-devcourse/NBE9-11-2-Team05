package com.team05.petmeeting.domain.user.dto.find;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyCodeReq(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Pattern(regexp = "\\d{6}")
        String code
) {
}
