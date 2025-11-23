package com.gb02.syumsvc.model.dto;

import java.util.HashMap;
import java.util.Map;

public class AlbumFavDTO implements DTO {
    private Integer idAlbum;
    private Integer idUsuario;

    public Integer getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(Integer idAlbum) {
        this.idAlbum = idAlbum;
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
        map.put("idAlbum", this.idAlbum);
        map.put("idUsuario", this.idUsuario);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        idAlbum = map.get("idAlbum") != null ? (Integer) map.get("idAlbum") : null;
        idUsuario = map.get("idUsuario") != null ? (Integer) map.get("idUsuario") : null;
    }

}
