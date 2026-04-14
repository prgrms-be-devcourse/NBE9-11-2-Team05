package com.team05.demo.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team05.demo.domain.user.errorCode.UserErrorCode;
import com.team05.demo.global.exception.ErrorCode;
import com.team05.demo.global.exception.ErrorResponse;
import com.team05.demo.global.security.errorCode.SecurityErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex
    ) throws IOException {

        ErrorCode errorCode;

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (ex instanceof CredentialsExpiredException) {
            errorCode = SecurityErrorCode.TOKEN_EXPIRED;
        } else if (ex instanceof BadCredentialsException) {
            errorCode = SecurityErrorCode.INVALID_TOKEN;
        } else {
            errorCode = UserErrorCode.UNAUTHORIZED;
        }

        ErrorResponse errorResponse = ErrorResponse.from(errorCode);

        response.getWriter().write(
                objectMapper.writeValueAsString(errorResponse)
        );
    }
}
