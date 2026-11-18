package com.sheilac.ecommerce.utils.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum ErrorCode {
    BAD_REQUEST("400", HttpStatus.BAD_REQUEST),
    NOT_FOUND("404", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("401", HttpStatus.UNAUTHORIZED),
    METHOD_NOT_ALLOWED("405", HttpStatus.METHOD_NOT_ALLOWED),
    CONFLICT("409", HttpStatus.CONFLICT),
    CONTENT_TOO_LARGE("413", HttpStatus.CONTENT_TOO_LARGE),
    UNSUPPORTED_MEDIA_TYPE("415", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    INTERNAL_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR);

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
