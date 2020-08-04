package com.kakao.pay.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SpreadMoneyReceived {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long spreadMoneyDistributionId;
    private long userId;
    private BigDecimal receivedAmount;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedDateTime;

    public SpreadMoneyReceived() {
    }

    public SpreadMoneyReceived(long userId) {
        this.userId = userId;
    }

    public long getSpreadMoneyDistributionId() {
        return spreadMoneyDistributionId;
    }

    public void setSpreadMoneyDistributionId(long spreadMoneyDistributionId) {
        this.spreadMoneyDistributionId = spreadMoneyDistributionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(String receivedAmount) {
        this.receivedAmount = new BigDecimal(receivedAmount);
    }

    public LocalDateTime getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(LocalDateTime receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }
}
