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

import com.mycompany.bioskop.entity.Pembayaran;

/**
 *
 * @author bill
 */
public class PembayaranRepository {
    public List<Pembayaran> getAllPembayaran(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pembayaran");
            ResultSet rs = stmt.executeQuery();
        ){
            List<Pembayaran> pembayarans = new ArrayList<>();
            while (rs.next()){
                int idPembayaran = rs.getInt("id_pembayaran");
                int idPemesanan = rs.getInt("id_pemesanan");
                String metode = rs.getString("metode");
                double jumlah = rs.getDouble("jumlah");
                LocalDateTime tanggalBayar = rs.getObject("tanggal_bayar", LocalDateTime.class);
                String status = rs.getString("status");

                Pembayaran pembayaran = new Pembayaran(idPembayaran, idPemesanan, metode, jumlah, tanggalBayar, status);
                pembayarans.add(pembayaran);
            }
            return pembayarans;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public Pembayaran getPembayaran(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pembayaran WHERE id_pembayaran = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idPembayaran = rs.getInt("id_pembayaran");
                    int idPemesanan = rs.getInt("id_pemesanan");
                    String metode = rs.getString("metode");
                    double jumlah = rs.getDouble("jumlah");
                    LocalDateTime tanggalBayar = rs.getObject("tanggal_bayar", LocalDateTime.class);
                    String status = rs.getString("status");

                    return new Pembayaran(idPembayaran, idPemesanan, metode, jumlah, tanggalBayar, status);
                }
            }
            return new Pembayaran();
        }catch(SQLException e){
            e.printStackTrace();
            return new Pembayaran();
        }
    }

    public void deletePembayaran(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM pembayaran WHERE id_pembayaran = ?");
        ){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void addPembayaran(int idPemesanan, String metode, double jumlah, LocalDateTime tanggalBayar, String status){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO pembayaran (id_pemesanan, metode, jumlah, tanggal_bayar, status) VALUES (?, ?, ?, ?, ?)")
                ){
            stmt.setInt(1, idPemesanan);
            stmt.setString(2, metode);
            stmt.setDouble(3, jumlah);
            stmt.setObject(4, tanggalBayar);
            stmt.setString(5, status);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updatePembayaran(int idPemesanan, String metode, double jumlah, LocalDateTime tanggalBayar, String status, int idPembayaran){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE pembayaran SET id_pemesanan = ?, metode = ?, jumlah = ?, tanggal_bayar = ?, status = ? WHERE id_pembayaran = ?");
        ){
            stmt.setInt(1, idPemesanan);
            stmt.setString(2, metode);
            stmt.setDouble(3, jumlah);
            stmt.setObject(4, tanggalBayar);
            stmt.setString(5, status);
            stmt.setInt(6, idPembayaran);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
