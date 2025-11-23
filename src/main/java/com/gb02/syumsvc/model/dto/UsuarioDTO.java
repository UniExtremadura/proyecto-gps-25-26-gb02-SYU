package com.gb02.syumsvc.model.dto;

import java.sql.Date;
import java.util.Map;

public class UsuarioDTO implements DTO {
    private Integer userId;
    private String username;
    private String name;
    private String firstLastName;
    private String secondLastName;
    private Date regDate;
    private String email;
    private String password;
    private Integer relatedArtist;
    private String image;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRelatedArtist() {
        return relatedArtist;
    }

    public void setRelatedArtist(Integer relatedArtist) {
        this.relatedArtist = relatedArtist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("userId", this.userId);
        map.put("username", this.username);
        map.put("name", this.name);
        map.put("firstLastName", this.firstLastName);
        map.put("secondLastName", this.secondLastName);
        map.put("regDate", this.regDate);
        map.put("email", this.email);
        map.put("password", this.password);
        map.put("relatedArtist", this.relatedArtist);
        map.put("image", this.image);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        userId = map.get("userId") != null ? (Integer) map.get("userId") : null;
        username = map.get("username") != null ? (String) map.get("username") : null;
        name = map.get("name") != null ? (String) map.get("name") : null;
        firstLastName = map.get("firstLastName") != null ? (String) map.get("firstLastName") : null;
        secondLastName = map.get("secondLastName") != null ? (String) map.get("secondLastName") : null;
        regDate = map.get("regDate") != null ? (Date) map.get("regDate") : null;
        email = map.get("email") != null ? (String) map.get("email") : null;
        password = map.get("password") != null ? (String) map.get("password") : null;
        relatedArtist = map.get("relatedArtist") != null ? (Integer) map.get("relatedArtist") : null;
        image = map.get("image") != null ? (String) map.get("image") : null;
    }
}
