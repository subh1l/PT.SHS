/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bioskop.entity;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author ACER NITRO
 */
public class Riwayat {
    private int idPemesanan;
    private int idUser;
    private String film;
    private LocalDate tanggal;
    private LocalTime jam;
    private String studio;
    private String kursi;
    private double totalHarga;
    private String status;

    public Riwayat() {
    }
    
    public Riwayat(int idPemesanan, int idUser, String film,
            LocalDate tanggal, LocalTime jam, String studio,
            String kursi, double totalHarga, String status) {
    

        this.idPemesanan = idPemesanan;
        this.idUser = idUser;
        this.film = film;
        this.tanggal = tanggal;
        this.jam = jam;
        this.studio = studio;
        this.kursi = kursi;
        this.totalHarga = totalHarga;
        this.status = status;
    }

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getFilm() {
        return film;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public LocalTime getJam() {
        return jam;
    }
    
    public String getStudio() {
        return studio;
    }

    public String getKursi() {
        return kursi;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public String getStatus() {
        return status;
    }
    
    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public void setFilm(String film) {
        this.film = film;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public void setJam(LocalTime jam) {
        this.jam = jam;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }
    
    public void setKursi(String kursi) {
        this.kursi = kursi;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
