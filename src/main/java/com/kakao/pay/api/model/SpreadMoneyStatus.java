package com.kakao.pay.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kakao.pay.api.enums.Currency;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonPropertyOrder({"registeredTime", "currency", "totalSpreadAmount", "totalReceivedAmount", "receivedInfo"})
public class SpreadMoneyStatus {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long spreadMoneyEventId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long userId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String roomId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Currency currency;
    @JsonProperty(value = "totalSpreadAmount", access = JsonProperty.Access.READ_ONLY)
    private BigDecimal totalAmount;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registeredTime;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<SpreadMoneyReceived> spreadMoneyReceivedList;

    public long getSpreadMoneyEventId() {
        return spreadMoneyEventId;
    }

    public void setSpreadMoneyEventId(long spreadMoneyEventId) {
        this.spreadMoneyEventId = spreadMoneyEventId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = new BigDecimal(totalAmount);
    }

    public LocalDateTime getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(LocalDateTime registeredTime) {
        this.registeredTime = registeredTime;
    }

    public List<SpreadMoneyReceived> getSpreadMoneyReceivedList() {
        return spreadMoneyReceivedList;
    }

    public void setSpreadMoneyReceivedList(List<SpreadMoneyReceived> spreadMoneyReceivedList) {
        this.spreadMoneyReceivedList = spreadMoneyReceivedList;
    }

    @JsonIgnore
    public List<SpreadMoneyReceived> getSpreadMoneyNotReceivedInfoList() {
        if (CollectionUtils.isEmpty(spreadMoneyReceivedList)) {
            return new ArrayList<>();
        }

        return spreadMoneyReceivedList.stream()
                .filter(spreadMoneyReceived -> spreadMoneyReceived.getUserId() == NumberUtils.LONG_ZERO && Objects.isNull(spreadMoneyReceived.getReceivedDateTime()))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public BigDecimal getDistributedAmount(long spreadMoneyDistributionId) {
        for (SpreadMoneyReceived spreadMoneyReceived : spreadMoneyReceivedList) {
            if (spreadMoneyReceived.getSpreadMoneyDistributionId() == spreadMoneyDistributionId) {
                return spreadMoneyReceived.getReceivedAmount();
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalReceivedAmount() {
        if (CollectionUtils.isEmpty(spreadMoneyReceivedList)) {
            return BigDecimal.ZERO;
        }

        return spreadMoneyReceivedList.stream()
                .filter(spreadMoneyReceived -> spreadMoneyReceived.getUserId() != NumberUtils.LONG_ZERO && Objects.nonNull(spreadMoneyReceived.getReceivedDateTime()))
                .map(SpreadMoneyReceived::getReceivedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<SpreadMoneyReceived> getReceivedInfo() {
        if (CollectionUtils.isEmpty(spreadMoneyReceivedList)) {
            return new ArrayList<>();
        }

        return spreadMoneyReceivedList.stream()
                .filter(spreadMoneyReceived -> spreadMoneyReceived.getUserId() != NumberUtils.LONG_ZERO && Objects.nonNull(spreadMoneyReceived.getReceivedDateTime()))
                .sorted(Comparator.comparing(SpreadMoneyReceived::getUserId)).collect(Collectors.toList());
    }
}
