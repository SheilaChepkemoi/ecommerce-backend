package com.sheilac.ecommerce.utils.exceptions;

import com.sheilac.ecommerce.utils.enums.ErrorCode;

public class FileStorageException extends ApiException {

    public FileStorageException(String message) {
        super(message, ErrorCode.INTERNAL_ERROR);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, ErrorCode.INTERNAL_ERROR);
        initCause(cause);
    }
}
