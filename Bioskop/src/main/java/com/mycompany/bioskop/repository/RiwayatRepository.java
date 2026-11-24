/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bioskop.repository;
import com.mycompany.bioskop.entity.Riwayat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ACER NITRO
 */
public class RiwayatRepository {
    public List<Riwayat> getRiwayatByUser(int idUser) {
        List<Riwayat> riwayatList = new ArrayList<>();
        String sql = "SELECT * FROM view_riwayat WHERE id_user = ?";

        try (
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idUser);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    LocalDate tanggal = rs.getDate("tanggal").toLocalDate();
                    LocalTime jam = rs.getTime("jam").toLocalTime();

                    Riwayat riwayat = new Riwayat(
                        rs.getInt("id_pemesanan"),
                        rs.getInt("id_user"),
                        rs.getString("film"),
                        tanggal,
                        jam,
                        rs.getString("studio"),
                        rs.getString("kursi"),
                        rs.getDouble("total_harga"),
                        rs.getString("status")
                    );

                    riwayatList.add(riwayat);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return riwayatList;
    }
    
}
