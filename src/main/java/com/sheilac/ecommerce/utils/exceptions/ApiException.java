package com.sheilac.ecommerce.utils.exceptions;

import com.sheilac.ecommerce.utils.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{

    private final ErrorCode errorCode;

    public ApiException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


}
