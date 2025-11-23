package com.gb02.syumsvc.model.dto;

import java.util.Map;

public interface DTO {
    public Map<String, Object> toMap();
    public void fromMap(Map<String, Object> map);
}
