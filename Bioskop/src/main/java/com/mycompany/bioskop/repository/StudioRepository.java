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

import com.mycompany.bioskop.entity.Studio;

/**
 *
 * @author bill
 */
public class StudioRepository {
    public List<Studio> getAllStudio(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM studio");
            ResultSet rs = stmt.executeQuery();
        ){
            List<Studio> studios = new ArrayList<>();
            while (rs.next()){
                int idStudio = rs.getInt("id_studio");
                String namaStudio = rs.getString("nama_studio");
                String tipe = rs.getString("tipe");
                int kapasitas = rs.getInt("kapasitas");

                Studio studio = new Studio(idStudio, namaStudio, tipe, kapasitas);
                studios.add(studio);
            }
            return studios;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public Studio getStudio(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM studio WHERE id_studio = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idStudio = rs.getInt("id_studio");
                    String namaStudio = rs.getString("nama_studio");
                    String tipe = rs.getString("tipe");
                    int kapasitas = rs.getInt("kapasitas");

                    return new Studio(idStudio, namaStudio, tipe, kapasitas);
                }
            }
            return new Studio();
        }catch(SQLException e){
            e.printStackTrace();
            return new Studio();
        }
    }

    public void deleteStudio(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM studio WHERE id_studio = ?");
        ){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void addStudio(String namaStudio, String tipe, int kapasitas){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO studio (nama_studio, tipe, kapasitas) VALUES (?, ?, ?)")
                ){
            stmt.setString(1, namaStudio);
            stmt.setString(2, tipe);
            stmt.setInt(3, kapasitas);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateStudio(String namaStudio, String tipe, int kapasitas, int idStudio){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE studio SET nama_studio = ?, tipe = ?, kapasitas = ? WHERE id_studio = ?");
        ){
            stmt.setString(1, namaStudio);
            stmt.setString(2, tipe);
            stmt.setInt(3, kapasitas);
            stmt.setInt(4, idStudio);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public int getStudioAmount() {
        try (
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM studio");
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
