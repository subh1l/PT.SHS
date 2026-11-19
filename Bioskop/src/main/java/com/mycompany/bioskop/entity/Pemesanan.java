/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.entity;

import java.time.LocalDateTime;

/**
 *
 * @author bill
 */
public class Pemesanan {
    private int idPemesanan;
    private int idUser;
    private int idJadwal;
    private LocalDateTime tanggalPesan;
    private double totalHarga;
    private String status;

    public Pemesanan() {
    }

    public Pemesanan(int idPemesanan, int idUser, int idJadwal, LocalDateTime tanggalPesan, double totalHarga, String status) {
        this.idPemesanan = idPemesanan;
        this.idUser = idUser;
        this.idJadwal = idJadwal;
        this.tanggalPesan = tanggalPesan;
        this.totalHarga = totalHarga;
        this.status = status;
    }

    public int getIdJadwal() {
        return idJadwal;
    }

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTanggalPesan() {
        return tanggalPesan;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setIdJadwal(int idJadwal) {
        this.idJadwal = idJadwal;
    }

    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTanggalPesan(LocalDateTime tanggalPesan) {
        this.tanggalPesan = tanggalPesan;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }
}
