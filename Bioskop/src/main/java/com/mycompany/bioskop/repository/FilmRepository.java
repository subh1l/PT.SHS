package com.mycompany.bioskop.repository;

import com.mycompany.bioskop.entity.Film;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmRepository {
    public List<Film> getAllFilm(){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM film");
            ResultSet rs = stmt.executeQuery();
        ){
            List<Film> films = new ArrayList<>();
            while (rs.next()){
                int id  = rs.getInt("id_film");
                String judul = rs.getString("judul");
                String genre = rs.getString("genre");
                int durasi = rs.getInt("durasi_menit");
                String rating = rs.getString("rating_usia");
                String sinopsis = rs.getString("sinopsis");
                String status = rs.getString("status");

                Film film = new Film(id, judul, genre, durasi, rating, sinopsis, status);
                films.add(film);
            }
            return films;
        }catch(SQLException e){
            e.printStackTrace();
            return List.of();
        }
    }

    public Film getFilm(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM film WHERE id_film = ?");
        ){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idFilm  = rs.getInt("id_film");
                    String judul = rs.getString("judul");
                    String genre = rs.getString("genre");
                    int durasi = rs.getInt("durasi_menit");
                    String rating = rs.getString("rating_usia");
                    String sinopsis = rs.getString("sinopsis");
                    String status = rs.getString("status");

                    return new Film(idFilm, judul, genre, durasi, rating, sinopsis, status); 
                }
            }
            return new Film();
        }catch(SQLException e){
            e.printStackTrace();
            return new Film();
        }
    }

    public void deleteFilm(int id){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM film WHERE id_film = ?");
        ){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void addFilm(Film f){
        try (
                Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO film (judul, genre, durasi_menit, rating_usia, sinopsis, status) VALUES (?, ?, ?, ?, ?, ?)")
                ){
            stmt.setString(1, f.getJudul());
            stmt.setString(2, f.getGenre());
            stmt.setInt(3, f.getDurasiMenit());
            stmt.setString(4, f.getRatingUsia());
            stmt.setString(5, f.getSinopsis());
            stmt.setString(6, f.getStatus());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateFilm(Film f){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE film SET judul = ?, genre = ?, durasi_menit = ?, rating_usia = ?, sinopsis = ?, status = ? WHERE id_film = ?");
        ){
            stmt.setString(1, f.getJudul());
            stmt.setString(2, f.getGenre());
            stmt.setInt(3, f.getDurasiMenit());
            stmt.setString(4, f.getRatingUsia());
            stmt.setString(5, f.getSinopsis());
            stmt.setString(6, f.getStatus());
            stmt.setInt(7, f.getIdFilm());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public int getAmount() {
        try (
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM film");
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

    public boolean existsByJudul(String judul){
        try(
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM film WHERE judul = ?");
        ){
            stmt.setString(1, judul);
            try(ResultSet rs = stmt.executeQuery()){
                return rs.next(); 
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}