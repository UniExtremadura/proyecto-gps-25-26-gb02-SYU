package com.gb02.syumsvc.model.dto;

import java.util.HashMap;
import java.util.Map;

public class CancionFavDTO implements DTO {
    private Integer idCancion;
    private Integer idUsuario;

    public Integer getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(Integer idCancion) {
        this.idCancion = idCancion;
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
        map.put("idCancion", this.idCancion);
        map.put("idUsuario", this.idUsuario);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        idCancion = map.get("idCancion") != null ? (Integer) map.get("idCancion") : null;
        idUsuario = map.get("idUsuario") != null ? (Integer) map.get("idUsuario") : null;
    }

}
