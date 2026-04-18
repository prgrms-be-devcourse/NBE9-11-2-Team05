package com.team05.petmeeting.domain.user.dto.find;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindIdReq(
        @Email
        @NotBlank
        String email
) {
}
