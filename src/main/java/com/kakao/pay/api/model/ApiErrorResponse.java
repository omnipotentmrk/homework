package com.kakao.pay.api.model;

import com.kakao.pay.api.exception.ApiRuntimeException;

public class ApiErrorResponse {
    private int errorCode;
    private String errorMessage;

    public ApiErrorResponse(ApiRuntimeException apiRuntimeException) {
        errorCode = apiRuntimeException.getResultCode();
        errorMessage = apiRuntimeException.getResultMessage();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
