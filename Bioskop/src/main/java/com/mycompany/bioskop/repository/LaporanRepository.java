/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.repository;

import com.mycompany.bioskop.model.Laporan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bill
 */
public class LaporanRepository {
    public List<Laporan> getAllLaporan(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM laporan");
            ResultSet rs = stmt.executeQuery();
        ){
            List<Laporan> reports = new ArrayList<>();
            while (rs.next()){
                LocalDate tanggal = rs.getObject("tanggal", LocalDate.class);
                String film = rs.getString("film");
                String studio = rs.getString("studio");
                LocalTime jam = rs.getObject("jam_tayang", LocalTime.class);
                int jumlah = rs.getInt("jumlah_tiket");
                double totalHarga = rs.getDouble("total_harga");

                Laporan laporan = new Laporan(tanggal, film, studio, jam, jumlah, totalHarga);
                reports.add(laporan);
            }
            return reports;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }
    
    public List<Laporan> getLaporan(LocalDate start, LocalDate end){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM laporan where tanggal >= ? AND tanggal <= ?");
        ){
            stmt.setObject(1, start);
            stmt.setObject(2, end);
            List<Laporan> reports = new ArrayList<>();
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    LocalDate tanggal = rs.getObject("tanggal", LocalDate.class);
                    String film = rs.getString("film");
                    String studio = rs.getString("studio");
                    LocalTime jam = rs.getObject("jam_tayang", LocalTime.class);
                    int jumlah = rs.getInt("jumlah_tiket");
                    double totalHarga = rs.getDouble("total_harga");

                    Laporan laporan = new Laporan(tanggal, film, studio, jam, jumlah, totalHarga);
                    reports.add(laporan);
                }
            }
            return reports;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }
}
