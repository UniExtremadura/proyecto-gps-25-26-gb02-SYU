package com.gb02.syumsvc.model.factory;

import com.gb02.syumsvc.model.dao.AlbumFavDAO;
import com.gb02.syumsvc.model.dao.ArtistaFavDAO;
import com.gb02.syumsvc.model.dao.CancionFavDAO;
import com.gb02.syumsvc.model.dao.MerchFavDAO;
import com.gb02.syumsvc.model.dao.SesionDAO;
import com.gb02.syumsvc.model.dao.UsuarioDAO;

public interface DAOFactory {
    public AlbumFavDAO getAlbumFavDao();
    public ArtistaFavDAO getArtistaFavDao();
    public CancionFavDAO getCancionFavDao();
    public MerchFavDAO getMerchFavDao();
    public SesionDAO getSesionDao();
    public UsuarioDAO getUsuarioDao(); 
}
