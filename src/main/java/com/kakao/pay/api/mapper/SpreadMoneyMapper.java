package com.kakao.pay.api.mapper;

import com.kakao.pay.api.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpreadMoneyMapper {
    void insertSpreadMoneyEvent(@Param("spreadMoneyEvent") SpreadMoneyEvent spreadMoneyEvent);

    void insertSpreadMoneyReceiveInfoList(@Param("spreadMoneyEventId") long spreadMoneyEventId,
                                          @Param("spreadMoneyDistributionList") List<SpreadMoneyDistribution> spreadMoneyDistributionList);

    SpreadMoneyStatus selectSpreadMoneyStatus(@Param("token") String token);

    void insertSpreadMoneyReceived(@Param("spreadMoneyReceived") SpreadMoneyReceived spreadMoneyReceived, @Param("spreadMoneyEventId") long spreadMoneyEventId);

    int updateSpreadMoneyDistribution(@Param("spreadMoneyDistributionId") long spreadMoneyDistributionId);
}
