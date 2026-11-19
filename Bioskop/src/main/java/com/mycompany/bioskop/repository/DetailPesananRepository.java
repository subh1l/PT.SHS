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

import com.mycompany.bioskop.entity.DetailPemesanan;

/**
 *
 * @author bill
 */
public class DetailPesananRepository {
    public List<DetailPemesanan> getAllDetails(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM detail_pemesanan");
            ResultSet rs = stmt.executeQuery();
        ){
            List<DetailPemesanan> details = new ArrayList<>();
            while (rs.next()){
                int idDetail = rs.getInt("id_detail");
                int pemesanan = rs.getInt("id_pemesanan");
                int jadwal = rs.getInt("id_jadwal");
                int kursi = rs.getInt("id_kursi");
                double harga = rs.getDouble("harga");

                DetailPemesanan detail = new DetailPemesanan(idDetail, pemesanan, jadwal, kursi, harga);
                details.add(detail);
            }
            return details;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public DetailPemesanan getDetail(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM detail_pemesanan WHERE id_detail = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idDetail = rs.getInt("id_detail");
                    int pemesanan = rs.getInt("id_pemesanan");
                    int jadwal = rs.getInt("id_jadwal");
                    int kursi = rs.getInt("id_kursi");
                    double harga = rs.getDouble("harga");

                    return new DetailPemesanan(idDetail, pemesanan, jadwal, kursi, harga);
                }
            }
            return new DetailPemesanan();
        }catch(SQLException e){
            e.printStackTrace();
            return new DetailPemesanan();
        }
    }

    public void deleteDetail(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM detail_pemesanan WHERE id_detail = ?");
        ){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void addDetail(int pemesanan, int jadwal, int kursi, double harga){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO detail_pemesanan (id_pemesanan, id_jadwal, id_kursi, harga) VALUES (?, ?, ?, ?)")
                ){
            stmt.setInt(1, pemesanan);
            stmt.setInt(2, jadwal);
            stmt.setInt(3, kursi);
            stmt.setDouble(4, harga);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateDetail(int pemesanan, int jadwal, int kursi, double harga, int detail){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE detail_pemesanan SET id_jadwal = ?, id_kursi = ?, harga = ? WHERE id_detail = ?");
        ){
            stmt.setInt(1, pemesanan);
            stmt.setInt(2, jadwal);
            stmt.setInt(3, kursi);
            stmt.setDouble(4, harga);
            stmt.setInt(5, detail);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
