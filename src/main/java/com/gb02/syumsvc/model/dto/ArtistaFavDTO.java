package com.gb02.syumsvc.model.dto;

import java.util.HashMap;
import java.util.Map;

public class ArtistaFavDTO implements DTO {
    private Integer idArtista;
    private Integer idUsuario;

    public Integer getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(Integer idArtista) {
        this.idArtista = idArtista;
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
        map.put("idArtista", this.idArtista);
        map.put("idUsuario", this.idUsuario);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        idArtista = map.get("idArtista") != null ? (Integer) map.get("idArtista") : null;
        idUsuario = map.get("idUsuario") != null ? (Integer) map.get("idUsuario") : null;
    }

}
