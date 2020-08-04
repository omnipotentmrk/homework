package com.kakao.pay.api.model;

import org.apache.commons.lang3.math.NumberUtils;

public class User {
    private long id;
    private String roomId;
    private String requestToken;

    public User() {
    }

    public User(long id, String roomId, String requestToken) {
        this.id = id;
        this.roomId = roomId;
        this.requestToken = requestToken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public boolean isValid() {
        return id != NumberUtils.LONG_ZERO;
    }
}
