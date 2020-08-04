package com.kakao.pay.api.enums;

public enum ErrorType {
    INVALID_HEADER(1001, "Invalid header."),
    NOT_EXIST_USER(1002, "This user is not exist."),
    NEED_TOKEN(1003, "This request should have token."),
    INVALID_TOKEN(1004, "This request should have valid token."),

    SPREAD_MONEY_RECEIVE_NOT_EXIST(3001, "This event is not exist."),
    SPREAD_MONEY_RECEIVE_ALREADY(3002, "This user already received."),
    SPREAD_MONEY_RECEIVE_INVALID_USER(3003, "Event owner cannot receive."),
    SPREAD_MONEY_RECEIVE_INVALID_ROOM(3004, "Cannot allow receive from another room user."),
    SPREAD_MONEY_RECEIVE_FULL(3005, "This event already full by others"),
    SPREAD_MONEY_RECEIVE_INVALID_TIME(3006, "This request time is not valid for receive money."),

    SPREAD_MONEY_STATUS_NOT_EXIST(4001, "Cannot return status from not exist event."),
    SPREAD_MONEY_STATUS_INVALID_USER(4002, "Can return for owner only."),
    SPREAD_MONEY_STATUS_INVALID_TIME(4003, "This request time is not valid for access status."),

    UNKNOWN(9999, "Unknown error was occurred.");

    private int code;
    private String message;

    ErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
