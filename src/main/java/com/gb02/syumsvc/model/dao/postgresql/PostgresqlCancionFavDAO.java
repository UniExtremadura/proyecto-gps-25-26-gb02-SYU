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
import com.gb02.syumsvc.model.dao.CancionFavDAO;
import com.gb02.syumsvc.model.dto.CancionFavDTO;

/**
 * PostgreSQL implementation of CancionFavDAO.
 * Handles database operations for favorite songs.
 */
public class PostgresqlCancionFavDAO implements CancionFavDAO {

    /**
     * Retrieves all favorite songs for a specific user.
     * 
     * @param idUsuario User ID
     * @return Array of CancionFavDTO objects
     */
    @Override
    public CancionFavDTO[] obtainCancionFavByUser(int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM CancionesFav WHERE idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idUsuario);
            ResultSet rset = ps.executeQuery();
            ArrayList<CancionFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                CancionFavDTO cf = new CancionFavDTO();
                cf.setIdCancion(rset.getInt("idCancion"));
                cf.setIdUsuario(rset.getInt("idUsuario"));
                results.add(cf);
            }
            return results.toArray(new CancionFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining canciones fav by idUsuario (PostgresqlCancionFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return new CancionFavDTO[0];
        }
    }

    /**
     * Retrieves all users who favorited a specific song.
     * 
     * @param idCancion Song ID
     * @return Array of CancionFavDTO objects
     */
    @Override
    public CancionFavDTO[] obtainCancionFavByCancion(int idCancion) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM CancionesFav WHERE idCancion = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idCancion);
            ResultSet rset = ps.executeQuery();
            ArrayList<CancionFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                CancionFavDTO cf = new CancionFavDTO();
                cf.setIdCancion(rset.getInt("idCancion"));
                cf.setIdUsuario(rset.getInt("idUsuario"));
                results.add(cf);
            }
            return results.toArray(new CancionFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining cancion fav by idCancion (PostgresqlCancionFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return new CancionFavDTO[0];
        }
    }

    /**
     * Retrieves all favorite songs.
     * 
     * @return Array of all CancionFavDTO objects
     */
    @Override
    public CancionFavDTO[] obtainCancionFav() {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM CancionesFav";
            Statement s = connection.createStatement();
            ResultSet rset = s.executeQuery(query);
            ArrayList<CancionFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                CancionFavDTO cf = new CancionFavDTO();
                cf.setIdCancion(rset.getInt("idCancion"));
                cf.setIdUsuario(rset.getInt("idUsuario"));
                results.add(cf);
            }
            return results.toArray(new CancionFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining all cancion fav (PostgresqlCancionFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return new CancionFavDTO[0];
        }
    }

    /**
     * Checks if a specific song is favorited by a specific user.
     * 
     * @param idCancion Song ID
     * @param idUsuario User ID
     * @return CancionFavDTO if exists, null otherwise
     */
    @Override
    public CancionFavDTO obtainCancionFav(int idCancion, int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM CancionesFav WHERE idCancion = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idCancion);
            ps.setInt(2, idUsuario);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                CancionFavDTO cf = new CancionFavDTO();
                cf.setIdCancion(rset.getInt("idCancion"));
                cf.setIdUsuario(rset.getInt("idUsuario"));
                return cf;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error obtaining cancion fav (PostgresqlCancionFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return null;
        }
    }

    /**
     * Adds a song to user's favorites.
     * 
     * @param cancionFav CancionFavDTO with song and user IDs
     * @return true if successful
     * @throws FavAlreadyExistsException if favorite already exists
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public boolean insertCancionFav(CancionFavDTO cancionFav) throws FavAlreadyExistsException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "INSERT INTO CancionesFav (idCancion, idUsuario) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, cancionFav.getIdCancion());
            ps.setInt(2, cancionFav.getIdUsuario());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new UnexpectedErrorException("Insert did not affect any rows");
            }
            return true;
        } catch (PSQLException e) {
            // Handle UNIQUE constraint violations (SQL state 23505)
            if ("23505".equals(e.getSQLState())) {
                throw new FavAlreadyExistsException("Song " + cancionFav.getIdCancion() + " is already favorited by user " + cancionFav.getIdUsuario());
            }
            System.err.println("PostgreSQL error inserting cancion fav: " + e.getMessage());
             
            throw new UnexpectedErrorException("Unexpected error inserting cancion fav: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inserting cancion fav (PostgresqlCancionFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            throw new UnexpectedErrorException("Unexpected error inserting cancion fav: " + e.getMessage());
        }
    }

    /**
     * Removes a song from user's favorites.
     * 
     * @param idCancion Song ID
     * @param idUsuario User ID
     * @return true if successful
     * @throws FavNotFoundException if favorite doesn't exist
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public boolean deleteCancionFav(int idCancion, int idUsuario) throws FavNotFoundException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "DELETE FROM CancionesFav WHERE idCancion = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idCancion);
            ps.setInt(2, idUsuario);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new FavNotFoundException("Song " + idCancion + " is not favorited by user " + idUsuario);
            }
            return true;
        } catch (FavNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error deleting cancion fav (PostgresqlCancionFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            throw new UnexpectedErrorException("Unexpected error deleting cancion fav: " + e.getMessage());
        }
    }
}
