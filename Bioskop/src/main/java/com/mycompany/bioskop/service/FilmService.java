package com.mycompany.bioskop.service;

import com.mycompany.bioskop.exception.ValidasiException;
import com.mycompany.bioskop.model.Film;
import com.mycompany.bioskop.repository.FilmRepository;
import java.sql.SQLException;
import java.util.List;

public class FilmService {
    private final FilmRepository repo;

    public FilmService(FilmRepository repo) { this.repo = repo; }

    public Film tambah(String judul, String genre, String durasiStr,
                       String rating, String sinopsis, String status)
            throws ValidasiException, SQLException {

        judul = judul == null ? "" : judul.trim();
        genre = genre == null ? "" : genre.trim();

        if (judul.isEmpty()) throw new ValidasiException("Judul tidak boleh kosong.");
        if (judul.length() > 150) throw new ValidasiException("Judul maksimal 150 karakter.");
        if (genre.isEmpty()) throw new ValidasiException("Genre tidak boleh kosong.");

        int durasi;
        try { durasi = Integer.parseInt(durasiStr.trim()); }
        catch (NumberFormatException e) { throw new ValidasiException("Durasi harus berupa angka."); }
        if (durasi <= 0 || durasi > 600) throw new ValidasiException("Durasi harus 1-600 menit.");

        if (repo.existsByJudul(judul)) throw new ValidasiException("Film dengan judul ini sudah ada.");

        Film f = new Film(0, judul, genre, durasi, rating,
                          sinopsis == null ? "" : sinopsis.trim(), status);
        repo.save(f);
        return f;
    }

    public List<Film> semua() throws SQLException { return repo.findAll(); }
}