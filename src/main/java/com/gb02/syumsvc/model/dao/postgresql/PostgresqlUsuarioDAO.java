package com.gb02.syumsvc.model.dao.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.gb02.syumsvc.exceptions.DupedEmailException;
import com.gb02.syumsvc.exceptions.DupedUsernameException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.exceptions.UserNotFoundException;
import com.gb02.syumsvc.model.dao.UsuarioDAO;
import com.gb02.syumsvc.model.dto.UsuarioDTO;

/**
 * PostgreSQL implementation of UsuarioDAO.
 * Handles database operations for User entities.
 */
public class PostgresqlUsuarioDAO implements UsuarioDAO {

    /**
     * Maps a ResultSet row to a UsuarioDTO object.
     * 
     * @param rset ResultSet positioned at a valid row
     * @return UsuarioDTO populated with data from the current row
     * @throws Exception if database access error occurs
     */
    private UsuarioDTO mapResultSetToUsuario(ResultSet rset) throws Exception {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setUserId(rset.getInt("idUsuario"));
        usuario.setUsername(rset.getString("nick"));
        usuario.setName(rset.getString("nombre"));
        usuario.setFirstLastName(rset.getString("apellido1"));
        usuario.setSecondLastName(rset.getString("apellido2"));
        usuario.setRegDate(rset.getDate("fechaReg"));
        usuario.setEmail(rset.getString("email"));
        usuario.setPassword(rset.getString("contrasena"));
        
        // Handle nullable idArtista field
        Integer idArtista = (Integer) rset.getObject("idArtista");
        usuario.setRelatedArtist(idArtista);
        
        // Handle nullable imagen field (bytea) - convert to Base64 String
        String imagen = rset.getString("imagen");
        if (imagen != null) {
            usuario.setImage(imagen);
        } else {
            usuario.setImage(null);
        }
        
        return usuario;
    }

    /**
     * Retrieves all users from the database.
     * 
     * @return Array of all UsuarioDTO objects, empty array if error occurs
     */
    @Override
    public UsuarioDTO[] obtainUsuarios() {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM Usuarios";
            Statement statement = connection.createStatement();
            ResultSet rset = statement.executeQuery(query);
            ArrayList<UsuarioDTO> results = new ArrayList<>();
            while (rset.next()) {
                results.add(mapResultSetToUsuario(rset));
            }
            return results.toArray(new UsuarioDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining usuarios (PostgresqlUsuarioDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return new UsuarioDTO[0];
        }
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param idUsuario User ID to search for
     * @return UsuarioDTO object
     * @throws UserNotFoundException if user with given ID doesn't exist
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public UsuarioDTO obtainUsuario(int idUsuario) throws UserNotFoundException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM Usuarios WHERE idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idUsuario);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                return mapResultSetToUsuario(rset);
            } else {
                throw new UserNotFoundException("Usuario con idUsuario " + idUsuario + " no encontrado.");
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error obtaining usuario by idUsuario (PostgresqlUsuarioDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error obtaining usuario: " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by their username (nick).
     * 
     * @param nick Username to search for
     * @return UsuarioDTO object
     * @throws UserNotFoundException if user with given nick doesn't exist
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public UsuarioDTO obtainUsuarioByNick(String nick) throws UserNotFoundException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM Usuarios WHERE nick = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, nick);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                return mapResultSetToUsuario(rset);
            } else {
                throw new UserNotFoundException("Usuario con nick " + nick + " no encontrado.");
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error obtaining usuario by nick (PostgresqlUsuarioDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error obtaining usuario: " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by their email address.
     * 
     * @param mail Email address to search for
     * @return UsuarioDTO object
     * @throws UserNotFoundException if user with given email doesn't exist
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public UsuarioDTO obtainUsuarioByMail(String mail) throws UserNotFoundException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM Usuarios WHERE email = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, mail);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                return mapResultSetToUsuario(rset);
            } else {
                throw new UserNotFoundException("Usuario con email " + mail + " no encontrado.");
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error obtaining usuario by email (PostgresqlUsuarioDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error obtaining usuario: " + e.getMessage());
        }
    }

    /**
     * Inserts a new user into the database.
     * 
     * @param usuario UsuarioDTO object with user data
     * @return Generated user ID
     * @throws DupedUsernameException if username already exists
     * @throws DupedEmailException if email already exists
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public int insertUsuario(UsuarioDTO usuario) throws UnexpectedErrorException, DupedUsernameException, DupedEmailException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "INSERT INTO Usuarios (nick, nombre, apellido1, apellido2, email, contrasena, idArtista, imagen) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getName());
            ps.setString(3, usuario.getFirstLastName());
            ps.setString(4, usuario.getSecondLastName());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getPassword());
            if (usuario.getRelatedArtist() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, usuario.getRelatedArtist());
            }
            if (usuario.getImage() == null) {
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else {
                ps.setString(8, usuario.getImage());
            }
            
            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new UnexpectedErrorException("Insert did not affect exactly one row");
            }
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            } else {
                throw new UnexpectedErrorException("Failed to retrieve generated key");
            }
        } catch (PSQLException e) {
            // Handle UNIQUE constraint violations (SQL state 23505)
            if ("23505".equals(e.getSQLState())) {
                String serverError = e.getServerErrorMessage().getDetail();
                if (serverError.contains("nick")) {
                    throw new DupedUsernameException("Username " + usuario.getUsername() + " already exists");
                }
                if (serverError.contains("email")) {
                    throw new DupedEmailException("Email " + usuario.getEmail() + " already exists");
                }
            }
            System.err.println("PostgreSQL error inserting usuario: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error inserting usuario: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inserting usuario (PostgresqlUsuarioDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error inserting usuario: " + e.getMessage());
        }
    }

    /**
     * Updates an existing user's information.
     * 
     * @param idUsuario ID of the user to update
     * @param usuario UsuarioDTO with new user data
     * @return true if update successful, false otherwise
     */
    @Override
    public boolean modifyUsuario(int idUsuario, UsuarioDTO usuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "UPDATE Usuarios SET nick = ?, nombre = ?, apellido1 = ?, apellido2 = ?, fechaReg = ?, email = ?, contrasena = ?, idArtista = ?, imagen = ? WHERE idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getName());
            ps.setString(3, usuario.getFirstLastName());
            ps.setString(4, usuario.getSecondLastName());
            ps.setDate(5, usuario.getRegDate());
            ps.setString(6, usuario.getEmail());
            ps.setString(7, usuario.getPassword());
            if (usuario.getRelatedArtist() == null) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, usuario.getRelatedArtist());
            }
            if (usuario.getImage() == null) {
                ps.setNull(9, java.sql.Types.VARCHAR);
            } else {
                ps.setString(9, usuario.getImage());
            }
            ps.setInt(10, idUsuario);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Error modifying usuario (PostgresqlUsuarioDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            return false;
        }
    }

    /**
     * Deletes a user from the database.
     * 
     * @param idUsuario ID of the user to delete
     * @return true if deletion successful
     * @throws UserNotFoundException if user doesn't exist
     * @throws UnexpectedErrorException if database error occurs
     */
    @Override
    public boolean deleteUsuario(int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "DELETE FROM Usuarios WHERE idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idUsuario);
            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException("Usuario with idUsuario " + idUsuario + " not found.");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting usuario (PostgresqlUsuarioDAO)");
            System.err.println("Reason: " + e.getMessage());
            
            throw new UnexpectedErrorException("Unexpected error deleting usuario: " + e.getMessage());
        }
    }
}
