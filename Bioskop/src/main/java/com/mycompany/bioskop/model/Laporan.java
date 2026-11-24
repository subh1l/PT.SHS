/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author bill
 */
public class Laporan {
    private LocalDate tanggal;
    private String film;
    private String studio;
    private LocalTime jam;
    private int jumlah;
    private double harga;

    public Laporan() {
    }

    public Laporan(LocalDate tanggal, String film, String studio, LocalTime jam, int jumlah, double harga) {
        this.tanggal = tanggal;
        this.film = film;
        this.studio = studio;
        this.jam = jam;
        this.jumlah = jumlah;
        this.harga = harga;
    }

    public String getFilm() {
        return film;
    }

    public double getHarga() {
        return harga;
    }

    public LocalTime getJam() {
        return jam;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getStudio() {
        return studio;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setFilm(String film) {
        this.film = film;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setJam(LocalTime jam) {
        this.jam = jam;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }
}