package com.gb02.syumsvc.model.dao;

import com.gb02.syumsvc.model.dto.SesionDTO;

public interface SesionDAO {
    public SesionDTO[] obtainSesiones();
    public SesionDTO obtainSesion(int idSesion);
    public SesionDTO obtainSesion(String token);
    public boolean insertSesion(SesionDTO sesion);
    public boolean modifySesion(int idSesion, SesionDTO sesion);
    public boolean deleteSesion(int idSesion);
}
