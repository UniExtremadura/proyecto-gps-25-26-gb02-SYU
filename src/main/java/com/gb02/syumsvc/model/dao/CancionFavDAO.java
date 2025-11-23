package com.gb02.syumsvc.model.dao;

import com.gb02.syumsvc.exceptions.FavAlreadyExistsException;
import com.gb02.syumsvc.exceptions.FavNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.model.dto.CancionFavDTO;

public interface CancionFavDAO {
    public CancionFavDTO[] obtainCancionFavByUser(int userId);
    public CancionFavDTO[] obtainCancionFavByCancion(int cancionId);
    public CancionFavDTO[] obtainCancionFav();
    public CancionFavDTO obtainCancionFav(int idCancion, int idUsuario);
    public boolean insertCancionFav(CancionFavDTO cancionFav) throws FavAlreadyExistsException, UnexpectedErrorException;
    public boolean deleteCancionFav(int idCancion, int idUsuario) throws FavNotFoundException, UnexpectedErrorException;
}
