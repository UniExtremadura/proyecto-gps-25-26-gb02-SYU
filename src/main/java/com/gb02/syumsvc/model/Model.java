package com.gb02.syumsvc.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gb02.syumsvc.model.factory.DAOFactory;
import com.gb02.syumsvc.model.dao.postgresql.PostgresqlDAOFactory;
import com.gb02.syumsvc.exceptions.DupedEmailException;
import com.gb02.syumsvc.exceptions.DupedUsernameException;
import com.gb02.syumsvc.exceptions.FavAlreadyExistsException;
import com.gb02.syumsvc.exceptions.FavNotFoundException;
import com.gb02.syumsvc.exceptions.SessionExpiredException;
import com.gb02.syumsvc.exceptions.SessionNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.exceptions.UserNotFoundException;
import com.gb02.syumsvc.model.dao.UsuarioDAO;
import com.gb02.syumsvc.model.dto.AlbumFavDTO;
import com.gb02.syumsvc.model.dto.ArtistaFavDTO;
import com.gb02.syumsvc.model.dto.CancionFavDTO;
import com.gb02.syumsvc.model.dto.SesionDTO;
import com.gb02.syumsvc.model.dto.UsuarioDTO;

@Service
public class Model {
    
    private static Model model = null;
    DAOFactory df;
    
    public static Model getModel(){
        if(model == null){
            model = new Model();
        }
        return model;
    }

    public Model() {
        df = new PostgresqlDAOFactory();
    }

    @Transactional
    public UsuarioDTO registrarUsuario(UsuarioDTO usuario) throws DupedEmailException, DupedUsernameException, UnexpectedErrorException {
        try {
            UsuarioDAO usuarioDao = df.getUsuarioDao();
            int idRegistrado = usuarioDao.insertUsuario(usuario);
            UsuarioDTO registrado = usuarioDao.obtainUsuario(idRegistrado);
            if(registrado != null){
                return registrado;
            }
            System.out.println("Error registering user.");
            throw new UnexpectedErrorException("Unexpected error while registering user: couldn't recover registered user.");
        } catch (DupedEmailException e){
            throw e;
        } catch (DupedUsernameException e){
            throw e;
        } catch (UnexpectedErrorException e){
            throw e;
        } catch (Exception e){
            System.out.println("Unexpected error in registrarUsuario: " + e.getMessage());
            throw new UnexpectedErrorException("Unexpected error in registrarUsuario: " + e.getMessage());
        }
    }

    public UsuarioDTO getUsuario(int idUsuario) throws UserNotFoundException, UnexpectedErrorException{
        try{
            UsuarioDAO usuarioDAO = df.getUsuarioDao();
            return usuarioDAO.obtainUsuario(idUsuario);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (UnexpectedErrorException e) {
            throw e;
        } catch (Exception e){
            throw new UnexpectedErrorException("Unexpected error in getUsuario: " + e.getMessage());
        }
    }
    
    public UsuarioDTO getUsuarioByNick(String nick) throws UserNotFoundException, UnexpectedErrorException{
        try{
            UsuarioDAO usuarioDAO = df.getUsuarioDao();
            return usuarioDAO.obtainUsuarioByNick(nick);
        } catch (UserNotFoundException unfe) {
            throw unfe;
        } catch (UnexpectedErrorException uee) {
            throw uee;
        } catch (Exception e){
            throw new UnexpectedErrorException("Unexpected error in getUsuarioByNick: " + e.getMessage());
        }
    }

    public UsuarioDTO getUsuarioByMail(String mail) throws UserNotFoundException, UnexpectedErrorException{
        try{
            UsuarioDAO usuarioDAO = df.getUsuarioDao();
            return usuarioDAO.obtainUsuarioByMail(mail);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (UnexpectedErrorException e) {
            throw e;
        } catch (Exception e){
            System.out.println("Unexpected error in getUsuario: " + e.getMessage());
            throw new UnexpectedErrorException("Unexpected error in getUsuario: " + e.getMessage());
        }
    }

    @Transactional
    public void insertarSesion(SesionDTO sesion) throws UnexpectedErrorException {
        try{
            df.getSesionDao().insertSesion(sesion);
        } catch (UnexpectedErrorException e){
            System.out.println("Unexpected error in insertarSesion: " + e.getMessage());
            throw e;
        }
    }

    public SesionDTO getSessionByToken(String token) throws UnexpectedErrorException, SessionNotFoundException, SessionExpiredException {
        try{
            SesionDTO sesion = df.getSesionDao().obtainSesion(token);
            java.util.Date now = new java.util.Date();
            if(sesion.getExpirationDate().before(new java.sql.Date(now.getTime()))){
                throw new SessionExpiredException("Session has expired");
            }
            return sesion;
        } catch (SessionExpiredException e){
            throw e;
        } catch (SessionNotFoundException e){
            throw e;
        } catch (UnexpectedErrorException e){
            throw e;
        } catch (Exception e){
            System.out.println("Unexpected error in getSessionByToken: " + e.getMessage());
            throw new UnexpectedErrorException("Unexpected error in getSessionByToken: " + e.getMessage());
        }
    }

    @Transactional
    public boolean deleteSesion(int idSesion) throws UnexpectedErrorException, SessionNotFoundException {
        try{
            return df.getSesionDao().deleteSesion(idSesion);
        } catch (SessionNotFoundException e){
            throw e;
        } catch (UnexpectedErrorException e){
            throw e;
        } catch (Exception e){
            System.out.println("Unexpected error in deleteSession: " + e.getMessage());
            throw new UnexpectedErrorException("Unexpected error in deleteSession: " + e.getMessage());
        }
    }

    @Transactional
    public boolean deleteUsuario(int idUsuario) throws UnexpectedErrorException, UserNotFoundException {
        try{
            return df.getUsuarioDao().deleteUsuario(idUsuario);
        } catch (UserNotFoundException e){
            throw e;
        } catch (UnexpectedErrorException e){
            throw e;
        } catch (Exception e){
            System.out.println("Unexpected error in deleteUsuario: " + e.getMessage());
            throw new UnexpectedErrorException("Unexpected error in deleteUsuario: " + e.getMessage());
        }
    }

    @Transactional
    public boolean updateUsuario(int idUsuario, UsuarioDTO usuario) throws UnexpectedErrorException, UserNotFoundException {
        try{
            return df.getUsuarioDao().modifyUsuario(idUsuario, usuario);
        } catch (UserNotFoundException e){
            throw e;
        } catch (UnexpectedErrorException e){
            throw e;
        } catch (Exception e){
            System.out.println("Unexpected error in updateUsuario: " + e.getMessage());
            throw new UnexpectedErrorException("Unexpected error in updateUsuario: " + e.getMessage());
        }
    }

    // ==================== FAVORITES OPERATIONS ====================

    /**
     * Gets all favorite songs for a specific user.
     * 
     * @param userId The ID of the user
     * @return Array of CancionFavDTO objects
     */
    public CancionFavDTO[] getCancionesFavByUser(int userId) {
        return df.getCancionFavDao().obtainCancionFavByUser(userId);
    }

    /**
     * Inserts a new favorite song for a user.
     * 
     * @param fav The CancionFavDTO to insert
     * @return true if the operation was successful
     * @throws FavAlreadyExistsException if the song is already favorited by the user
     * @throws UnexpectedErrorException if an unexpected error occurs
     */
    @Transactional
    public boolean insertCancionFav(CancionFavDTO fav) throws FavAlreadyExistsException, UnexpectedErrorException {
        try {
            return df.getCancionFavDao().insertCancionFav(fav);
        } catch (FavAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    /**
     * Deletes a favorite song for a user.
     * 
     * @param idCancion The ID of the song
     * @param idUsuario The ID of the user
     * @return true if the operation was successful
     * @throws FavNotFoundException if the song is not favorited by the user
     * @throws UnexpectedErrorException if an unexpected error occurs
     */
    @Transactional
    public boolean deleteCancionFav(int idCancion, int idUsuario) throws FavNotFoundException, UnexpectedErrorException {
        try {
            return df.getCancionFavDao().deleteCancionFav(idCancion, idUsuario);
        } catch (FavNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    /**
     * Gets all favorite artists for a specific user.
     * 
     * @param userId The ID of the user
     * @return Array of ArtistaFavDTO objects
     */
    public ArtistaFavDTO[] getArtistasFavByUser(int userId) {
        return df.getArtistaFavDao().obtainArtistaFavByUser(userId);
    }

    /**
     * Inserts a new favorite artist for a user.
     * 
     * @param fav The ArtistaFavDTO to insert
     * @return true if the operation was successful
     * @throws FavAlreadyExistsException if the artist is already favorited by the user
     * @throws UnexpectedErrorException if an unexpected error occurs
     */
    @Transactional
    public boolean insertArtistaFav(ArtistaFavDTO fav) throws FavAlreadyExistsException, UnexpectedErrorException {
        try {
            return df.getArtistaFavDao().insertArtistaFav(fav);
        } catch (FavAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    /**
     * Deletes a favorite artist for a user.
     * 
     * @param idArtista The ID of the artist
     * @param idUsuario The ID of the user
     * @return true if the operation was successful
     * @throws FavNotFoundException if the artist is not favorited by the user
     * @throws UnexpectedErrorException if an unexpected error occurs
     */
    @Transactional
    public boolean deleteArtistaFav(int idArtista, int idUsuario) throws FavNotFoundException, UnexpectedErrorException {
        try {
            return df.getArtistaFavDao().deleteArtistaFav(idArtista, idUsuario);
        } catch (FavNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    /**
     * Gets all favorite albums for a specific user.
     * 
     * @param userId The ID of the user
     * @return Array of AlbumFavDTO objects
     */
    public AlbumFavDTO[] getAlbumesFavByUser(int userId) {
        return df.getAlbumFavDao().obtainAlbumFavByUser(userId);
    }

    /**
     * Inserts a new favorite album for a user.
     * 
     * @param fav The AlbumFavDTO to insert
     * @return true if the operation was successful
     * @throws FavAlreadyExistsException if the album is already favorited by the user
     * @throws UnexpectedErrorException if an unexpected error occurs
     */
    @Transactional
    public boolean insertAlbumFav(AlbumFavDTO fav) throws FavAlreadyExistsException, UnexpectedErrorException {
        try {
            return df.getAlbumFavDao().insertAlbumFav(fav);
        } catch (FavAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    /**
     * Deletes a favorite album for a user.
     * 
     * @param idAlbum The ID of the album
     * @param idUsuario The ID of the user
     * @return true if the operation was successful
     * @throws FavNotFoundException if the album is not favorited by the user
     * @throws UnexpectedErrorException if an unexpected error occurs
     */
    @Transactional
    public boolean deleteAlbumFav(int idAlbum, int idUsuario) throws FavNotFoundException, UnexpectedErrorException {
        try {
            return df.getAlbumFavDao().deleteAlbumFav(idAlbum, idUsuario);
        } catch (FavNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

}
