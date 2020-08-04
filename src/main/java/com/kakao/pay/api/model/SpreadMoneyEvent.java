package com.kakao.pay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakao.pay.api.enums.Currency;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

public class SpreadMoneyEvent {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long userId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String roomId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int targetMemberCount;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Currency currency = Currency.KRW;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private BigDecimal totalAmount;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getTargetMemberCount() {
        return targetMemberCount;
    }

    public void setTargetMemberCount(int targetMemberCount) {
        this.targetMemberCount = targetMemberCount;
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

    @JsonIgnore
    public String getTotalAmountString() {
        return totalAmount.toString();
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = new BigDecimal(totalAmount);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonIgnore
    public boolean isValidRequestParam() {
        return totalAmount.compareTo(BigDecimal.ZERO) > NumberUtils.INTEGER_ZERO && targetMemberCount > NumberUtils.INTEGER_ZERO;
    }
}
