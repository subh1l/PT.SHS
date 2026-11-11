/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bioskop.repository;

import com.mycompany.bioskop.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserRepository {

    public void save(User user) throws SQLException {
        String sql = "INSERT INTO user (nama, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = Databaseutil.getConnection(); PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, user.getNama());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getPassword());
            pst.setString(4, user.getRole());
            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setIdUser(rs.getInt(1));
                }
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM user WHERE email = ?";
        try (Connection conn = Databaseutil.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }
}
