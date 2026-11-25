/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.bioskop.entity.Kursi;

/**
 *
 * @author bill
 */
public class KursiRepository {
    public List<Kursi> getAllKursi(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM kursi");
            ResultSet rs = stmt.executeQuery();
        ){
            List<Kursi> kursis = new ArrayList<>();
            while (rs.next()){
                int idKursi = rs.getInt("id_kursi");
                int idStudio = rs.getInt("id_studio");
                String baris = rs.getString("baris");
                int nomor = rs.getInt("nomor");

                Kursi kursi = new Kursi(idKursi, idStudio, baris, nomor);
                kursis.add(kursi);
            }
            return kursis;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public Kursi getKursi(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM kursi WHERE id_kursi = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idKursi = rs.getInt("id_kursi");
                    int idStudio = rs.getInt("id_studio");
                    String baris = rs.getString("baris");
                    int nomor = rs.getInt("nomor");

                    return new Kursi(idKursi, idStudio, baris, nomor);
                }
            }
            return new Kursi();
        }catch(SQLException e){
            e.printStackTrace();
            return new Kursi();
        }
    }

    public void deleteKursi(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM kursi WHERE id_kursi = ?");
        ){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void addKursi(int idStudio, String baris, int nomor){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO kursi (id_studio, baris, nomor) VALUES (?, ?, ?)")
                ){
            stmt.setInt(1, idStudio);
            stmt.setString(2, baris);
            stmt.setInt(3, nomor);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void addKursi(Connection conn, int idStudio, String baris, int nomor)
            throws SQLException {

        String sql = "INSERT INTO kursi (id_studio, baris, nomor) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idStudio);
            stmt.setString(2, baris);
            stmt.setInt(3, nomor);
            stmt.executeUpdate();
        }
    }

    public void updateKursi(int idStudio, String baris, int nomor, int idKursi){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE kursi SET id_studio = ?, baris = ?, nomor = ? WHERE id_kursi = ?");
        ){
            stmt.setInt(1, idStudio);
            stmt.setString(2, baris);
            stmt.setInt(3, nomor);
            stmt.setInt(4, idKursi);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
