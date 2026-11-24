/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.bioskop.entity.Jadwal;
import java.time.LocalDate;

/**
 *
 * @author bill
 */
public class JadwalRepository {
    public List<Jadwal> getAllJadwal(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM jadwal");
            ResultSet rs = stmt.executeQuery();
        ){
            List<Jadwal> jadwals = new ArrayList<>();
            while (rs.next()){
                int id = rs.getInt("id_jadwal");
                int film = rs.getInt("id_film");
                int studio = rs.getInt("id_studio");
                LocalDate tanggal = rs.getObject("tanggal", LocalDate.class);
                LocalTime jamMulai = rs.getObject("jam_mulai", LocalTime.class);
                double harga = rs.getDouble("harga");

                Jadwal jadwal = new Jadwal(id, film, studio, tanggal, jamMulai, harga);
                jadwals.add(jadwal);
            }
            return jadwals;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public Jadwal getJadwal(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM jadwal WHERE id_jadwal = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idJadwal = rs.getInt("id_jadwal");
                    int film = rs.getInt("id_film");
                    int studio = rs.getInt("email");
                    LocalDate tanggal = rs.getObject("tanggal", LocalDate.class);
                    LocalTime jamMulai = rs.getObject("jam_mulai", LocalTime.class);
                    double harga = rs.getDouble("harga");

                    return new Jadwal(idJadwal, film, studio, tanggal, jamMulai, harga);
                }
            }
            return new Jadwal();
        }catch(SQLException e){
            e.printStackTrace();
            return new Jadwal();
        }
    }

    public void deleteJadwal(int id){
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
    
    public void addJadwal(int film, int studio, LocalDate tanggal, LocalTime mulai, double harga){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO jadwal (id_film, id_studio, tanggal, jam_mulai, harga) VALUES (?, ?, ?, ?, ?)")
                ){
            stmt.setInt(1, film);
            stmt.setInt(2, studio);
            stmt.setObject(3, tanggal);
            stmt.setObject(4, mulai);
            stmt.setDouble(5, harga);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateJadwal(int film, int studio, LocalDate tanggal, LocalTime mulai, double harga, int jadwal){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE jadwal SET id_film = ?, id_studio = ?, tanggal = ?, jam_mulai = ?, harga = ? WHERE id_jadwal = ?");
        ){
            stmt.setInt(1, film);
            stmt.setInt(2, studio);
            stmt.setObject(3, tanggal);
            stmt.setObject(4, mulai);
            stmt.setDouble(5, harga);
            stmt.setInt(6, jadwal);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public int getJadwalAmount(){
        try(
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM jadwal");
                ResultSet rs = stmt.executeQuery();
                ){
            int amount = rs.getInt(1);
            return amount;
        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
}
