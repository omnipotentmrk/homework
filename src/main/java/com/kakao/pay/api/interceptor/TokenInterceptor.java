package com.kakao.pay.api.interceptor;

import com.kakao.pay.api.constant.GeneralConstant;
import com.kakao.pay.api.enums.ErrorType;
import com.kakao.pay.api.exception.ApiRuntimeException;
import com.kakao.pay.api.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    private static final int TOKEN_LENGTH = 3;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User user = (User) request.getAttribute(GeneralConstant.HTTP_REQUEST_ATTRIBUTE_USER);

        try {
            this.checkRequestToken(user);
        } catch (ApiRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRuntimeException(ErrorType.UNKNOWN, e);
        }

        return true;
    }

    private void checkRequestToken(User user) {
        if (StringUtils.isEmpty(user.getRequestToken())) {
            throw new ApiRuntimeException(ErrorType.NEED_TOKEN);
        }

        if (user.getRequestToken().length() != TOKEN_LENGTH) {
            throw new ApiRuntimeException(ErrorType.INVALID_TOKEN);
        }
    }
}
