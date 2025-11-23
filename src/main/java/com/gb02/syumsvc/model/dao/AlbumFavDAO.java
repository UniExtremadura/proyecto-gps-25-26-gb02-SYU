package com.gb02.syumsvc.model.dao;

import com.gb02.syumsvc.exceptions.FavAlreadyExistsException;
import com.gb02.syumsvc.exceptions.FavNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.model.dto.AlbumFavDTO;

public interface AlbumFavDAO {
    public AlbumFavDTO[] obtainAlbumFavByUser(int userId);
    public AlbumFavDTO[] obtainAlbumFavByAlbum(int albumId);
    public AlbumFavDTO[] obtainAlbumFav();
    public AlbumFavDTO obtainAlbumFav(int idAlbum, int idUsuario);
    public boolean insertAlbumFav(AlbumFavDTO albumFav) throws FavAlreadyExistsException, UnexpectedErrorException;
    public boolean deleteAlbumFav(int idAlbum, int idUsuario) throws FavNotFoundException, UnexpectedErrorException;
}
