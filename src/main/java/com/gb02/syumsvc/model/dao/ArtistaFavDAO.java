package com.gb02.syumsvc.model.dao;

import com.gb02.syumsvc.exceptions.FavAlreadyExistsException;
import com.gb02.syumsvc.exceptions.FavNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.model.dto.ArtistaFavDTO;

public interface ArtistaFavDAO {
    public ArtistaFavDTO[] obtainArtistaFavByUser(int userId);
    public ArtistaFavDTO[] obtainArtistaFavByArtista(int artistaId);
    public ArtistaFavDTO[] obtainArtistaFav();
    public ArtistaFavDTO obtainArtistaFav(int idArtista, int idUsuario);
    public boolean insertArtistaFav(ArtistaFavDTO artistaFav) throws FavAlreadyExistsException, UnexpectedErrorException;
    public boolean deleteArtistaFav(int idArtista, int idUsuario) throws FavNotFoundException, UnexpectedErrorException;
}
