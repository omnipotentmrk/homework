package com.kakao.pay.api.exception;

import com.kakao.pay.api.enums.ErrorType;
import org.springframework.http.HttpStatus;

public class ApiRuntimeException extends RuntimeException {
    private boolean successful;
    private int resultCode;
    private String resultMessage;

    public ApiRuntimeException(Throwable cause) {
        super(cause);
    }

    public ApiRuntimeException(ErrorType errorType) {
        super(errorType.getMessage());

        successful = false;
        resultCode = errorType.getCode();
        resultMessage = errorType.getMessage();
    }

    public ApiRuntimeException(ErrorType errorType, Throwable cause) {
        super(cause);

        successful = false;
        resultCode = errorType.getCode();
        resultMessage = errorType.getMessage();
    }

    public ApiRuntimeException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());

        successful = false;
        resultCode = httpStatus.value();
        resultMessage = httpStatus.getReasonPhrase();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
