/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.entity;

/**
 *
 * @author bill
 */
public class Film {
    private int idFilm;
    private String judul;
    private String genre;
    private int durasiMenit;
    private String ratingUsia;
    private String sinopsis;
    private String status;

    public Film() {
    }

    public Film(int idFilm, String judul, String genre, int durasiMenit, String ratingUsia, String sinopsis, String status) {
        this.idFilm = idFilm;
        this.judul = judul;
        this.genre = genre;
        this.durasiMenit = durasiMenit;
        this.ratingUsia = ratingUsia;
        this.sinopsis = sinopsis;
        this.status = status;
    }

    public int getDurasiMenit() {
        return durasiMenit;
    }

    public String getGenre() {
        return genre;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public String getJudul() {
        return judul;
    }

    public String getRatingUsia() {
        return ratingUsia;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public String getStatus() {
        return status;
    }

    public void setDurasiMenit(int durasiMenit) {
        this.durasiMenit = durasiMenit;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setRatingUsia(String ratingUsia) {
        this.ratingUsia = ratingUsia;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
