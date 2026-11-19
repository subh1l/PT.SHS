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
public class Pembayaran {
    private int idPembayaran;
    private int idPemesanan;
    private String metode;
    private double jumlah;
    private LocalDateTime tanggalBayar;
    private String status;

    public Pembayaran() {
    }

    public Pembayaran(int idPembayaran, int idPemesanan, String metode, double jumlah, LocalDateTime tanggalBayar, String status) {
        this.idPembayaran = idPembayaran;
        this.idPemesanan = idPemesanan;
        this.metode = metode;
        this.jumlah = jumlah;
        this.tanggalBayar = tanggalBayar;
        this.status = status;
    }

    public int getIdPembayaran() {
        return idPembayaran;
    }

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public double getJumlah() {
        return jumlah;
    }

    public String getMetode() {
        return metode;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTanggalBayar() {
        return tanggalBayar;
    }

    public void setIdPembayaran(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTanggalBayar(LocalDateTime tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }
}
