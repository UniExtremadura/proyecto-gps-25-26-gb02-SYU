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
import com.gb02.syumsvc.model.dao.ArtistaFavDAO;
import com.gb02.syumsvc.model.dto.ArtistaFavDTO;

/**
 * PostgreSQL implementation of ArtistaFavDAO.
 * Handles database operations for favorite artists.
 */
public class PostgresqlArtistaFavDAO implements ArtistaFavDAO {
    @Override
    public ArtistaFavDTO[] obtainArtistaFavByUser(int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM ArtistasFav WHERE idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idUsuario);
            ResultSet rset = ps.executeQuery();
            ArrayList<ArtistaFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                ArtistaFavDTO af = new ArtistaFavDTO();
                af.setIdArtista(rset.getInt("idArtista"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                results.add(af);
            }
            return results.toArray(new ArtistaFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining artistas fav by idUsuario (PostgresqlArtistaFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return new ArtistaFavDTO[0];
        }
    }

    @Override
    public ArtistaFavDTO[] obtainArtistaFavByArtista(int idArtista) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM ArtistasFav WHERE idArtista = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idArtista);
            ResultSet rset = ps.executeQuery();
            ArrayList<ArtistaFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                ArtistaFavDTO af = new ArtistaFavDTO();
                af.setIdArtista(rset.getInt("idArtista"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                results.add(af);
            }
            return results.toArray(new ArtistaFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining artista fav by idArtista (PostgresqlArtistaFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return new ArtistaFavDTO[0];
        }
    }

    @Override
    public ArtistaFavDTO[] obtainArtistaFav() {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM ArtistasFav";
            Statement s = connection.createStatement();
            ResultSet rset = s.executeQuery(query);
            ArrayList<ArtistaFavDTO> results = new ArrayList<>();
            while (rset.next()) {
                ArtistaFavDTO af = new ArtistaFavDTO();
                af.setIdArtista(rset.getInt("idArtista"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                results.add(af);
            }
            return results.toArray(new ArtistaFavDTO[0]);
        } catch (Exception e) {
            System.err.println("Error obtaining all artista fav (PostgresqlArtistaFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return new ArtistaFavDTO[0];
        }
    }

    @Override
    public ArtistaFavDTO obtainArtistaFav(int idArtista, int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM ArtistasFav WHERE idArtista = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idArtista);
            ps.setInt(2, idUsuario);
            ResultSet rset = ps.executeQuery();
            if (rset.next()) {
                ArtistaFavDTO af = new ArtistaFavDTO();
                af.setIdArtista(rset.getInt("idArtista"));
                af.setIdUsuario(rset.getInt("idUsuario"));
                return af;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error obtaining artista fav (PostgresqlArtistaFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            return null;
        }
    }

    @Override
    public boolean insertArtistaFav(ArtistaFavDTO artistaFav) throws FavAlreadyExistsException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "INSERT INTO ArtistasFav (idArtista, idUsuario) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, artistaFav.getIdArtista());
            ps.setInt(2, artistaFav.getIdUsuario());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new UnexpectedErrorException("Insert did not affect any rows");
            }
            return true;
        } catch (PSQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new FavAlreadyExistsException("Artist " + artistaFav.getIdArtista() + " is already favorited by user " + artistaFav.getIdUsuario());
            }
            System.err.println("PostgreSQL error inserting artista fav: " + e.getMessage());
             
            throw new UnexpectedErrorException("Unexpected error inserting artista fav: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inserting artista fav (PostgresqlArtistaFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            throw new UnexpectedErrorException("Unexpected error inserting artista fav: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteArtistaFav(int idArtista, int idUsuario) throws FavNotFoundException, UnexpectedErrorException {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "DELETE FROM ArtistasFav WHERE idArtista = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idArtista);
            ps.setInt(2, idUsuario);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new FavNotFoundException("Artist " + idArtista + " is not favorited by user " + idUsuario);
            }
            return true;
        } catch (FavNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error deleting artista fav (PostgresqlArtistaFavDAO)");
            System.err.println("Reason: " + e.getMessage());
             
            throw new UnexpectedErrorException("Unexpected error deleting artista fav: " + e.getMessage());
        }
    }
}
