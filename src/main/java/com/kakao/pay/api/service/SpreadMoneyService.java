package com.kakao.pay.api.service;

import com.kakao.pay.api.enums.Currency;
import com.kakao.pay.api.enums.ErrorType;
import com.kakao.pay.api.exception.ApiRuntimeException;
import com.kakao.pay.api.mapper.SpreadMoneyMapper;
import com.kakao.pay.api.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SpreadMoneyService {
    private static final long DAYS_7 = 7;
    private static final long MINUTES_10 = 10;

    private final SpreadMoneyMapper spreadMoneyMapper;

    public SpreadMoneyService(SpreadMoneyMapper spreadMoneyMapper) {
        this.spreadMoneyMapper = spreadMoneyMapper;
    }

    @Transactional
    public SpreadMoneyEvent createEvent(SpreadMoneyEvent spreadMoneyEvent) {
        List<SpreadMoneyDistribution> spreadMoneyDistributionList = this.distributeMoney(spreadMoneyEvent);

        spreadMoneyMapper.insertSpreadMoneyEvent(spreadMoneyEvent);
        spreadMoneyMapper.insertSpreadMoneyReceiveInfoList(spreadMoneyEvent.getId(), spreadMoneyDistributionList);

        return spreadMoneyEvent;
    }

    public SpreadMoneyStatus getSpreadMoneyStatus(User user) {
        return spreadMoneyMapper.selectSpreadMoneyStatus(user.getRequestToken());
    }

    public void checkValidateReceive(User user, SpreadMoneyStatus spreadMoneyStatus) {
        if (Objects.isNull(spreadMoneyStatus)) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_NOT_EXIST);
        }

        if (spreadMoneyStatus.getSpreadMoneyReceivedList().stream().anyMatch(spreadMoneyReceived -> spreadMoneyReceived.getUserId() == user.getId())) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_ALREADY);
        }

        if (spreadMoneyStatus.getUserId() == user.getId()) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_INVALID_USER);
        }

        if (!StringUtils.equals(spreadMoneyStatus.getRoomId(), user.getRoomId())) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_INVALID_ROOM);
        }

        if (CollectionUtils.isEmpty(spreadMoneyStatus.getSpreadMoneyNotReceivedInfoList())) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_FULL);
        }

        if (LocalDateTime.now().isBefore(spreadMoneyStatus.getRegisteredTime()) || LocalDateTime.now().isAfter(spreadMoneyStatus.getRegisteredTime().plusMinutes(MINUTES_10))) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_INVALID_TIME);
        }
    }

    @Transactional
    public SpreadMoneyDistribution receive(User user, SpreadMoneyStatus spreadMoneyStatus) {
        SpreadMoneyReceived spreadMoneyReceived = new SpreadMoneyReceived(user.getId());

        try {
            spreadMoneyMapper.insertSpreadMoneyReceived(spreadMoneyReceived, spreadMoneyStatus.getSpreadMoneyEventId());
        } catch (DataIntegrityViolationException e) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_FULL);
        }

        if (spreadMoneyMapper.updateSpreadMoneyDistribution(spreadMoneyReceived.getSpreadMoneyDistributionId()) == NumberUtils.INTEGER_ZERO) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_RECEIVE_FULL);
        }

        return new SpreadMoneyDistribution(spreadMoneyReceived.getSpreadMoneyDistributionId(), spreadMoneyStatus.getDistributedAmount(spreadMoneyReceived.getSpreadMoneyDistributionId()));
    }

    public void checkValidateStatus(User user, SpreadMoneyStatus spreadMoneyStatus) {
        if (Objects.isNull(spreadMoneyStatus)) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_STATUS_NOT_EXIST);
        }

        if (spreadMoneyStatus.getUserId() != user.getId()) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_STATUS_INVALID_USER);
        }

        if (LocalDateTime.now().isBefore(spreadMoneyStatus.getRegisteredTime()) || LocalDateTime.now().isAfter(spreadMoneyStatus.getRegisteredTime().plusDays(DAYS_7))) {
            throw new ApiRuntimeException(ErrorType.SPREAD_MONEY_STATUS_INVALID_TIME);
        }
    }

    private List<SpreadMoneyDistribution> distributeMoney(SpreadMoneyEvent spreadMoneyEvent) {
        BigDecimal target = spreadMoneyEvent.getTotalAmount();
        List<SpreadMoneyDistribution> spreadMoneyDistributionList = new ArrayList<>();

        for (int i = 0; i < spreadMoneyEvent.getTargetMemberCount() - 1; i++) {
            BigDecimal randFromDouble = BigDecimal.valueOf(Math.random());
            BigDecimal actualRandomDec = randFromDouble.multiply(target).setScale(spreadMoneyEvent.getCurrency().equals(Currency.KRW) ? 0 : 2, BigDecimal.ROUND_DOWN);

            target = target.subtract(actualRandomDec);

            spreadMoneyDistributionList.add(new SpreadMoneyDistribution(actualRandomDec));
        }

        spreadMoneyDistributionList.add(new SpreadMoneyDistribution(target));

        return spreadMoneyDistributionList;
    }
}
