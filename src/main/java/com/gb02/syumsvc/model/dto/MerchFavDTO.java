package com.gb02.syumsvc.model.dto;

import java.util.HashMap;
import java.util.Map;

public class MerchFavDTO implements DTO {
    private Integer idMerch;
    private Integer idUsuario;

    public Integer getIdMerch() {
        return idMerch;
    }

    public void setIdMerch(Integer idMerch) {
        this.idMerch = idMerch;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }    

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("idMerch", this.idMerch);
        map.put("idUsuario", this.idUsuario);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        idMerch = map.get("idMerch") != null ? (Integer) map.get("idMerch") : null;
        idUsuario = map.get("idUsuario") != null ? (Integer) map.get("idUsuario") : null;
    }

}
