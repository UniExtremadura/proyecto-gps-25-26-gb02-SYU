package com.gb02.syumsvc.model.dao.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.gb02.syumsvc.model.dao.MerchFavDAO;
import com.gb02.syumsvc.model.dto.MerchFavDTO;

public class PostgresqlMerchFavDAO implements MerchFavDAO {

    @Override
    public MerchFavDTO[] obtainMerchFavByUser(int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM MerchFav WHERE idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idUsuario);
            ResultSet rset = ps.executeQuery();
            ArrayList<MerchFavDTO> results = new ArrayList<>();
            while(rset.next()){
                MerchFavDTO mf = new MerchFavDTO();
                mf.setIdMerch(rset.getInt(0));
                mf.setIdUsuario(rset.getInt(1));
                results.add(mf);
            }        
            return results.toArray(new MerchFavDTO[results.size()]);
        }catch(Exception e){
            System.err.println("Error obtaining merch fav by idUsuario (PostgresqlMerchFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            return new MerchFavDTO[0];
        }
    }

    @Override
    public MerchFavDTO[] obtainMerchFavByMerch(int idMerch) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM MerchFav WHERE idMerch = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idMerch);
            ResultSet rset = ps.executeQuery();
            ArrayList<MerchFavDTO> results = new ArrayList<>();
            while(rset.next()){
                MerchFavDTO mf = new MerchFavDTO();
                mf.setIdMerch(rset.getInt(0));
                mf.setIdUsuario(rset.getInt(1));
                results.add(mf);
            }        
            return results.toArray(new MerchFavDTO[results.size()]);
        }catch(Exception e){
            System.err.println("Error obtaining merch fav by idMerch (PostgresqlMerchFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            return new MerchFavDTO[0];
        }
    }

    @Override
    public MerchFavDTO[] obtainMerchFav() {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM MerchFav";
            Statement s = connection.createStatement();
            ResultSet rset = s.executeQuery(query);
            ArrayList<MerchFavDTO> results = new ArrayList<>();
            while(rset.next()){
                MerchFavDTO mf = new MerchFavDTO();
                mf.setIdMerch(rset.getInt(0));
                mf.setIdUsuario(rset.getInt(1));
                results.add(mf);
            }        
            return results.toArray(new MerchFavDTO[results.size()]);
        }catch(Exception e){
            System.err.println("Error obtaining all merch fav (PostgresqlMerchFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            return new MerchFavDTO[0];
        }
    }

    @Override
    public MerchFavDTO obtainMerchFav(int idMerch, int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "SELECT * FROM MerchFav WHERE idMerch = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idMerch);
            ps.setInt(2, idUsuario);
            ResultSet rset = ps.executeQuery();
            if(rset.next()){
                MerchFavDTO mf = new MerchFavDTO();
                mf.setIdMerch(rset.getInt(0));
                mf.setIdUsuario(rset.getInt(1));
                return mf;
            }        
            return null;
        }catch(Exception e){
            System.err.println("Error obtaining merch fav by idMerch (PostgresqlMerchFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean insertMerchFav(MerchFavDTO merchFav) {
        // TODO: Comprobación de integridad. ¿Existe el merch en el micro servicio TPP?
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "INSERT INTO MerchFav VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, merchFav.getIdMerch());
            ps.setInt(2, merchFav.getIdUsuario());
            if(ps.executeUpdate() == 0) return false;
            return true;
        }catch(Exception e){
            System.err.println("Error obtaining merch fav by idMerch (PostgresqlMerchFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteMerchFav(int idMerch, int idUsuario) {
        try (Connection connection = PostgresqlConnector.getConnection()) {
            String query = "DELETE FROM MerchFav WHERE idMerch = ? AND idUsuario = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idMerch);
            ps.setInt(2, idUsuario);
            if(ps.executeUpdate() == 0) return false;
            return true;
        }catch(Exception e){
            System.err.println("Error obtaining merch fav by idMerch (PostgresqlMerchFavDAO)");
            System.err.println("Reason: " + e.getMessage());
            return false;
        }
    }
    
}
