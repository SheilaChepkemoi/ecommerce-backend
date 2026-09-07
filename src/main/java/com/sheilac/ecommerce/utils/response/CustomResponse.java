package com.sheilac.ecommerce.utils.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class CustomResponse<T> {
        @Builder.Default
        private ResponseType responseType = ResponseType.SUCCESS;

        private Object generatedId;

        private String generatedCode;

        private T payload;

        private String exceptionClass;

        private String message;

        private ErrorCode errorCode;

        private Long messageTracker;
}
