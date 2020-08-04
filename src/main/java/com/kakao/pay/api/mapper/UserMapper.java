package com.kakao.pay.api.mapper;

import com.kakao.pay.api.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectUser(@Param("userId") long userId);
}
