package com.mycompany.bioskop.repository;

import com.mycompany.bioskop.entity.Film;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmRepository {

    public void save(Film f) throws SQLException {
        String sql = "INSERT INTO film (judul, genre, durasi_menit, rating_usia, sinopsis, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Databaseutil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, f.getJudul());
            p.setString(2, f.getGenre());
            p.setInt(3, f.getDurasiMenit());
            p.setString(4, f.getRatingUsia());
            p.setString(5, f.getSinopsis());
            p.setString(6, f.getStatus());
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) f.setIdFilm(rs.getInt(1));
            }
        }
    }

    public boolean existsByJudul(String judul) throws SQLException {
        String sql = "SELECT 1 FROM film WHERE judul = ?";
        try (Connection c = Databaseutil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, judul);
            try (ResultSet rs = p.executeQuery()) { return rs.next(); }
        }
    }

    public List<Film> findAll() throws SQLException {
        List<Film> list = new ArrayList<>();
        String sql = "SELECT * FROM film ORDER BY id_film DESC";
        try (Connection c = Databaseutil.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Film(rs.getInt("id_film"), rs.getString("judul"),
                        rs.getString("genre"), rs.getInt("durasi_menit"),
                        rs.getString("rating_usia"), rs.getString("sinopsis"),
                        rs.getString("status")));
            }
        }
        return list;
    }
}