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

import com.mycompany.bioskop.entity.User;

/**
 *
 * @author bill
 */
public class UserRepository {
    public List<User> getAllUser(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user");
            ResultSet rs = stmt.executeQuery();
        ){
            List<User> users = new ArrayList<>();
            while (rs.next()){
                int id  = rs.getInt("id_user");
                String nama = rs.getString("nama");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                LocalDateTime createdAt = rs.getObject("status", LocalDateTime.class);

                User user = new User(id, nama, email, password, role);
                users.add(user);
            }
            return users;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public User getUser(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE id_user = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idUser  = rs.getInt("id_user");
                    String nama = rs.getString("nama");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    LocalDateTime createdAt = rs.getObject("status", LocalDateTime.class);

                    return new User(idUser, nama, email, password, role);
                }
            }
            return new User();
        }catch(SQLException e){
            e.printStackTrace();
            return new User();
        }
    }

    public void deleteUser(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE id_user = ?");
        ){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void addUser(String nama, String email, String password, String role){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (nama, email, password, role) VALUES (?, ?, ?, ?)")
                ){
            stmt.setString(1, nama);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateUser(String nama, String email, String password, String role, int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE film SET nama = ?, email = ?, password = ?, role = ? WHERE id_user = ?");
        ){
            stmt.setString(1, nama);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean existsByEmail(String email){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM user WHERE email = ?")
            ){
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
