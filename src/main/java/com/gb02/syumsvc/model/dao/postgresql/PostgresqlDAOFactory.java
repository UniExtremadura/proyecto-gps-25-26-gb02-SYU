package com.gb02.syumsvc.model.dao.postgresql;

import com.gb02.syumsvc.model.dao.AlbumFavDAO;
import com.gb02.syumsvc.model.dao.ArtistaFavDAO;
import com.gb02.syumsvc.model.dao.CancionFavDAO;
import com.gb02.syumsvc.model.dao.MerchFavDAO;
import com.gb02.syumsvc.model.dao.SesionDAO;
import com.gb02.syumsvc.model.dao.UsuarioDAO;
import com.gb02.syumsvc.model.factory.DAOFactory;

public class PostgresqlDAOFactory implements DAOFactory {
    @Override
    public AlbumFavDAO getAlbumFavDao() {
        return new PostgresqlAlbumFavDAO();
    }

    @Override
    public ArtistaFavDAO getArtistaFavDao() {
        return new PostgresqlArtistaFavDAO();
    }

    @Override
    public CancionFavDAO getCancionFavDao() {
        return new PostgresqlCancionFavDAO();
    }

    @Override
    public MerchFavDAO getMerchFavDao() {
        return new PostgresqlMerchFavDAO();
    }

    @Override
    public SesionDAO getSesionDao() {
        return new PostgresqlSesionDAO();
    }

    @Override
    public UsuarioDAO getUsuarioDao() {
        return new PostgresqlUsuarioDAO();
    }
    
}
