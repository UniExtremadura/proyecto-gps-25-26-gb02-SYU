package com.gb02.syumsvc.model.dao.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.gb02.syumsvc.exceptions.SessionNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.model.dao.SesionDAO;
import com.gb02.syumsvc.model.dto.SesionDTO;

/**
 * PostgreSQL implementation of SesionDAO.
 * Handles database operations for Session entities.
 */
public class PostgresqlSesionDAO implements SesionDAO {

    /**
     * Maps a ResultSet row to a SesionDTO object.
     * 
     * @param rset ResultSet positioned at a valid row
     * @return SesionDTO populated with data from the current row
     * @throws Exception if database access error occurs
     */
    private SesionDTO mapResultSetToSesion(ResultSet rset) throws Exception {
        SesionDTO sesion = new SesionDTO();
        sesion.setId(rset.getInt("idSesion"));
        sesion.setToken(rset.getString("token"));
        sesion.setExpirationDate(rset.getDate("fechaValidez"));
        sesion.setUserId(rset.getInt("idUsuario"));
        return sesion;
    }

    /**
     * Retrieves all sessions from the database.
     * 
     * @return Array of all SesionDTO objects, empty array if error occurs
     */
    @Override
    public SesionDTO[] obtainSesiones() {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM Sesiones";
            Statement statement = connection.createStatement();
            ResultSet rset = statement.executeQuery(query);
            ArrayList<SesionDTO> results = new ArrayList<>();
            while (rset.next()) {
                results.add(mapResultSetToSesion(rset));
            }
            return results.toArray(new SesionDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining sesiones (PostgresqlSesionDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return new SesionDTO[0];
        }
    }

    /**
     * Retrieves a session by its ID.
     * 
     * @param idSesion Session ID to search for
     * @return SesionDTO object, or null if not found or error occurs
     */
    @Override
    public SesionDTO obtainSesion(int idSesion) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM Sesiones WHERE idSesion = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idSesion);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                return mapResultSetToSesion(rset);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error obtaining sesion by idSesion (PostgresqlSesionDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return null;
        }
    }

    /**
     * Retrieves a session by its token.
     * 
     * @param token Session token to search for
     * @return SesionDTO object
     * @throws SessionNotFoundException if token is null or session not found
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public SesionDTO obtainSesion(String token) throws SessionNotFoundException, UnexpectedErrorException {
        if (token == null) {
            throw new SessionNotFoundException("Session token is null");
        }
        
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM Sesiones WHERE token = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, token);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                return mapResultSetToSesion(rset);
            } else {
                throw new SessionNotFoundException("Session not found for token: " + token);
            }
        } catch (SessionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error obtaining sesion by token (PostgresqlSesionDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error obtaining session by token: " + token);
        }
    }

    /**
     * Inserts a new session into the database.
     * 
     * @param sesion SesionDTO object with session data
     * @return true if insertion successful
     * @throws UnexpectedErrorException if database error occurs or insertion fails
     */
    @Override
    public boolean insertSesion(SesionDTO sesion) throws UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "INSERT INTO Sesiones (token, fechaValidez, idUsuario) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, sesion.getToken());
            ps.setDate(2, sesion.getExpirationDate());
            ps.setInt(3, sesion.getUserId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new UnexpectedErrorException("Insert did not affect exactly one row");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error inserting sesion (PostgresqlSesionDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Error creating a session for user " + sesion.getUserId());
        }
    }

    /**
     * Modifies an existing session.
     * NOT IMPLEMENTED - Sessions are immutable once created.
     * 
     * @param idSesion Session ID to modify
     * @param sesion New session data
     * @return Not applicable
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean modifySesion(int idSesion, SesionDTO sesion) {
        throw new UnsupportedOperationException("Unimplemented method 'modifySesion' - Sessions are immutable");
    }

    /**
     * Deletes a session from the database (logout).
     * 
     * @param idSesion Session ID to delete
     * @return true if deletion successful
     * @throws SessionNotFoundException if session doesn't exist
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public boolean deleteSesion(int idSesion) throws SessionNotFoundException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "DELETE FROM Sesiones WHERE idSesion = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idSesion);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SessionNotFoundException("Session not found with id: " + idSesion);
            }
            return true;
        } catch (SessionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error deleting sesion (PostgresqlSesionDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error deleting session with id: " + idSesion);
        }
    }
}
