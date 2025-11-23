package com.gb02.syumsvc.model.dto;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class SesionDTO implements DTO {
    private Integer id;
    private String token;
    private Date expirationDate;
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("token", this.token);
        map.put("expirationDate", this.expirationDate);
        map.put("userId", this.userId);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        id = map.get("id") != null ? (Integer) map.get("id") : null;
        token = map.get("token") != null ? (String) map.get("token") : null;
        expirationDate = map.get("expirationDate") != null ? (Date) map.get("expirationDate") : null;
        userId = map.get("userId") != null ? (Integer) map.get("userId") : null;
    }

}