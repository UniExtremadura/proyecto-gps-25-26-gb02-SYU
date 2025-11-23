package com.gb02.syumsvc.model.dao.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.gb02.syumsvc.exceptions.FavAlreadyExistsException;
import com.gb02.syumsvc.exceptions.FavNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.model.dao.AlbumFavDAO;
import com.gb02.syumsvc.model.dto.AlbumFavDTO;

/**
 * PostgreSQL implementation of AlbumFavDAO.
 * Handles database operations for favorite albums.
 */
public class PostgresqlAlbumFavDAO implements AlbumFavDAO {
    @Override
    public AlbumFavDTO[] obtainAlbumFavByUser(int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM AlbumesFav WHERE idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idUsuario);
            ResultSet rset = ps.executeQuery();
            ArrayList<AlbumFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                AlbumFavDTO af = new AlbumFavDTO();
                af.setIdAlbum(rset.getInt("idAlbum"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                results.add(af);
            }
            return results.toArray(new AlbumFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining albumes fav by idUsuario (PostgresqlAlbumFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return new AlbumFavDTO[0];
        }
    }

    @Override
    public AlbumFavDTO[] obtainAlbumFavByAlbum(int idAlbum) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM AlbumesFav WHERE idAlbum = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idAlbum);
            ResultSet rset = ps.executeQuery();
            ArrayList<AlbumFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                AlbumFavDTO af = new AlbumFavDTO();
                af.setIdAlbum(rset.getInt("idAlbum"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                results.add(af);
            }
            return results.toArray(new AlbumFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining album fav by idAlbum (PostgresqlAlbumFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return new AlbumFavDTO[0];
        }
    }

    @Override
    public AlbumFavDTO[] obtainAlbumFav() {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM AlbumesFav";
            Statement s = connection.createStatement();
            ResultSet rset = s.executeQuery(query);
            ArrayList<AlbumFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                AlbumFavDTO af = new AlbumFavDTO();
                af.setIdAlbum(rset.getInt("idAlbum"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                results.add(af);
            }
            return results.toArray(new AlbumFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining all album fav (PostgresqlAlbumFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return new AlbumFavDTO[0];
        }
    }

    @Override
    public AlbumFavDTO obtainAlbumFav(int idAlbum, int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM AlbumesFav WHERE idAlbum = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idAlbum);
            ps.setInt(2, idUsuario);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                AlbumFavDTO af = new AlbumFavDTO();
                af.setIdAlbum(rset.getInt("idAlbum"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                return af;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error obtaining album fav (PostgresqlAlbumFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return null;
        }
    }

    @Override
    public boolean insertAlbumFav(AlbumFavDTO albumFav) throws FavAlreadyExistsException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "INSERT INTO AlbumesFav (idAlbum, idUsuario) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, albumFav.getIdAlbum());
            ps.setInt(2, albumFav.getIdUsuario());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new UnexpectedErrorException("Insert did not affect any rows");
            }
            return true;
        } catch (PSQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new FavAlreadyExistsException("Album " + albumFav.getIdAlbum() + " is already favorited by user " + albumFav.getIdUsuario());
            }
            System.err.println("PostgreSQL error inserting album fav: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error inserting album fav: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inserting album fav (PostgresqlAlbumFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error inserting album fav: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteAlbumFav(int idAlbum, int idUsuario) throws FavNotFoundException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "DELETE FROM AlbumesFav WHERE idAlbum = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idAlbum);
            ps.setInt(2, idUsuario);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new FavNotFoundException("Album " + idAlbum + " is not favorited by user " + idUsuario);
            }
            return true;
        } catch (FavNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error deleting album fav (PostgresqlAlbumFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error deleting album fav: " + e.getMessage());
        }
    }
}
