package com.team05.demo.global.exception;

public record ValidationError(
        String field,
        String reason
) {}