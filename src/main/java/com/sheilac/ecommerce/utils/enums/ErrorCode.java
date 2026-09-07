package com.sheilac.ecommerce.utils.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum ErrorCode {
    BAD_REQUEST("400", HttpStatus.BAD_REQUEST),
    NOT_FOUND("404", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("401", HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR),
    CONFLICT("409", HttpStatus.CONFLICT);

    private final String code;
    private final HttpStatus status;

    ErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    public static ErrorCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid error code: " + code));
    }

}
