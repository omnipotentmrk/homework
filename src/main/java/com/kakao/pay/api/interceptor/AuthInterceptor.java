package com.kakao.pay.api.interceptor;

import com.kakao.pay.api.constant.GeneralConstant;
import com.kakao.pay.api.enums.ErrorType;
import com.kakao.pay.api.exception.ApiRuntimeException;
import com.kakao.pay.api.mapper.UserMapper;
import com.kakao.pay.api.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final String HEADER_USER_ID = "X-USER-ID";
    private static final String HEADER_ROOM_ID = "X-ROOM-ID";
    private static final String HEADER_REQUEST_TOKEN = "token";

    private final UserMapper userMapper;

    public AuthInterceptor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User user = new User();

        try {
            this.checkHeader(request);
            this.checkExistUser(request);

            user.setId(NumberUtils.toLong(request.getHeader(HEADER_USER_ID)));
            user.setRoomId(request.getHeader(HEADER_ROOM_ID));

            if (StringUtils.isNoneEmpty(request.getHeader(HEADER_REQUEST_TOKEN))) {
                user.setRequestToken(request.getHeader(HEADER_REQUEST_TOKEN));
            }
        } catch (IllegalArgumentException e) {
            throw new ApiRuntimeException(ErrorType.INVALID_HEADER, e);
        } catch (ApiRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRuntimeException(ErrorType.UNKNOWN, e);
        } finally {
            request.setAttribute(GeneralConstant.HTTP_REQUEST_ATTRIBUTE_USER, user);
        }

        return true;
    }

    private void checkHeader(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getHeader(HEADER_USER_ID)) || StringUtils.isEmpty(request.getHeader(HEADER_ROOM_ID)) ||
                !NumberUtils.isParsable(request.getHeader(HEADER_USER_ID))) {
            throw new IllegalArgumentException();
        }
    }

    private void checkExistUser(HttpServletRequest request) {
        if (!userMapper.selectUser(NumberUtils.toLong(request.getHeader(HEADER_USER_ID))).isValid()) {
            throw new ApiRuntimeException(ErrorType.NOT_EXIST_USER);
        }
    }
}
