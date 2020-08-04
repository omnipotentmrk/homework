package com.kakao.pay.api.contorller.advice;

import com.kakao.pay.api.enums.ErrorType;
import com.kakao.pay.api.exception.ApiRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandlerController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ApiRuntimeException badRequest(HttpServletRequest request, Exception e) {
        this.printLog(request, e, false);

        return new ApiRuntimeException(HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiRuntimeException methodNotAllowed(HttpServletRequest request, Exception e) {
        this.printLog(request, e, false);

        return new ApiRuntimeException(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class, ConversionFailedException.class})
    public ApiRuntimeException notFound(HttpServletRequest request, Exception e) {
        this.printLog(request, e, false);

        return new ApiRuntimeException(HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiRuntimeException.class)
    public ApiRuntimeException apiRuntimeException(HttpServletRequest request, ApiRuntimeException e) {
        if (e.getResultCode() == ErrorType.INVALID_HEADER.getCode() || e.getResultCode() == ErrorType.INVALID_TOKEN.getCode() || e.getResultCode() == ErrorType.NEED_TOKEN.getCode()) {
            return this.badRequest(request, e);
        }

        this.printLog(request, e, true);

        return e;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiRuntimeException internalServerError(HttpServletRequest request, Exception e) {
        this.printLog(request, e, true);

        if (request.getRequestURI().startsWith("/spread-money")) {
            return new ApiRuntimeException(ErrorType.UNKNOWN);
        }

        return new ApiRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void printLog(HttpServletRequest request, Exception e, boolean isPrintStacktrace) {
        if (isPrintStacktrace) {
            LOGGER.error("uid => {} || method => {} || url => {} || param => {} || e => {}",
                    Objects.nonNull(request.getAttribute("user")) ? request.getAttribute("user").toString() : StringUtils.EMPTY,
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    e.getMessage(), e);
        } else {
            LOGGER.error("uid => {} || method => {} || url => {} || param => {} || e => {}",
                    Objects.nonNull(request.getAttribute("user")) ? request.getAttribute("user").toString() : StringUtils.EMPTY,
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    e.getMessage());
        }
    }
}
