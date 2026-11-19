/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.bioskop.entity.Pemesanan;

/**
 *
 * @author bill
 */
public class PemesananRepository {
    public List<Pemesanan> getAllPemesanan(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pemesanan");
            ResultSet rs = stmt.executeQuery();
        ){
            List<Pemesanan> pemesanans = new ArrayList<>();
            while (rs.next()){
                int idPemesanan = rs.getInt("id_pemesanan");
                int idUser = rs.getInt("id_user");
                int idJadwal = rs.getInt("id_jadwal");
                LocalDateTime tanggalPesan = rs.getObject("tanggal_pesan", LocalDateTime.class);
                double totalHarga = rs.getDouble("total_harga");
                String status = rs.getString("status");

                Pemesanan pemesanan = new Pemesanan(idPemesanan, idUser, idJadwal, tanggalPesan, totalHarga, status);
                pemesanans.add(pemesanan);
            }
            return pemesanans;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public Pemesanan getPemesanan(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pemesanan WHERE id_pemesanan = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idPemesanan = rs.getInt("id_pemesanan");
                    int idUser = rs.getInt("id_user");
                    int idJadwal = rs.getInt("id_jadwal");
                    LocalDateTime tanggalPesan = rs.getObject("tanggal_pesan", LocalDateTime.class);
                    double totalHarga = rs.getDouble("total_harga");
                    String status = rs.getString("status");

                    return new Pemesanan(idPemesanan, idUser, idJadwal, tanggalPesan, totalHarga, status);
                }
            }
            return new Pemesanan();
        }catch(SQLException e){
            e.printStackTrace();
            return new Pemesanan();
        }
    }

    public void deletePemesanan(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM pemesanan WHERE id_pemesanan = ?");
        ){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void addPemesanan(int idUser, int idJadwal, LocalDateTime tanggalPesan, double totalHarga, String status){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO pemesanan (id_user, id_jadwal, tanggal_pesan, total_harga, status) VALUES (?, ?, ?, ?, ?)")
                ){
            stmt.setInt(1, idUser);
            stmt.setInt(2, idJadwal);
            stmt.setObject(3, tanggalPesan);
            stmt.setDouble(4, totalHarga);
            stmt.setString(5, status);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updatePemesanan(int idUser, int idJadwal, LocalDateTime tanggalPesan, double totalHarga, String status, int idPemesanan){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE pemesanan SET id_user = ?, id_jadwal = ?, tanggal_pesan = ?, total_harga = ?, status = ? WHERE id_pemesanan = ?");
        ){
            stmt.setInt(1, idUser);
            stmt.setInt(2, idJadwal);
            stmt.setObject(3, tanggalPesan);
            stmt.setDouble(4, totalHarga);
            stmt.setString(5, status);
            stmt.setInt(6, idPemesanan);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
