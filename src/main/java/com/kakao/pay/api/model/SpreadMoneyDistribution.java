package com.kakao.pay.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class SpreadMoneyDistribution {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;
    private BigDecimal amount;

    public SpreadMoneyDistribution(long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public SpreadMoneyDistribution(BigDecimal amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
