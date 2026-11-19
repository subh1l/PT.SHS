/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.entity;

/**
 *
 * @author bill
 */
public class DetailPemesanan {
    private int idDetail;
    private int idPemesanan;
    private int idjadwal;
    private int idKursi;
    private double harga;

    public DetailPemesanan() {
    }

    public DetailPemesanan(int idDetail, int idPemesanan, int idjadwal, int idKursi, double harga) {
        this.idDetail = idDetail;
        this.idPemesanan = idPemesanan;
        this.idjadwal = idjadwal;
        this.idKursi = idKursi;
        this.harga = harga;
    }

    public double getHarga() {
        return harga;
    }

    public int getIdDetail() {
        return idDetail;
    }

    public int getIdKursi() {
        return idKursi;
    }

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public int getIdjadwal() {
        return idjadwal;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public void setIdKursi(int idKursi) {
        this.idKursi = idKursi;
    }

    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public void setIdjadwal(int idjadwal) {
        this.idjadwal = idjadwal;
    }    
}
