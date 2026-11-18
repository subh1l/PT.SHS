package com.mycompany.bioskop.model;

public class Film {
    private int idFilm;
    private String judul, genre, ratingUsia, sinopsis, status;
    private int durasiMenit;

    public Film(int idFilm, String judul, String genre, int durasiMenit,
                String ratingUsia, String sinopsis, String status) {
        this.idFilm = idFilm; this.judul = judul; this.genre = genre;
        this.durasiMenit = durasiMenit; this.ratingUsia = ratingUsia;
        this.sinopsis = sinopsis; this.status = status;
    }
    public int getIdFilm() { return idFilm; }
    public void setIdFilm(int id) { this.idFilm = id; }
    public String getJudul() { return judul; }
    public String getGenre() { return genre; }
    public int getDurasiMenit() { return durasiMenit; }
    public String getRatingUsia() { return ratingUsia; }
    public String getSinopsis() { return sinopsis; }
    public String getStatus() { return status; }
}