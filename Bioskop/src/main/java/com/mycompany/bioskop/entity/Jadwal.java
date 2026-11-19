/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author bill
 */
public class Jadwal {
    private int idJadwal;
    private int idFilm;
    private int idStudio;
    private LocalDateTime tanggal;
    private LocalTime jamMulai;
    private double harga;

    public Jadwal() {
    }

    public Jadwal(int idjadwal, int idFilm, int idStudio, LocalDateTime tanggal, LocalTime jamMulai, double harga) {
        this.idJadwal = idjadwal;
        this.idFilm = idFilm;
        this.idStudio = idStudio;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.harga = harga;
    }

    public double getHarga() {
        return harga;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public int getIdStudio() {
        return idStudio;
    }

    public int getIdJadwal() {
        return idJadwal;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public LocalDateTime getTanggal() {
        return tanggal;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    public void setIdStudio(int idStudio) {
        this.idStudio = idStudio;
    }

    public void setIdjadwal(int idjadwal) {
        this.idJadwal = idjadwal;
    }

    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = jamMulai;
    }

    public void setTanggal(LocalDateTime tanggal) {
        this.tanggal = tanggal;
    }
    
    
}
