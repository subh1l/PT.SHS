/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.entity;

/**
 *
 * @author bill
 */
public class Kursi {
    private int idKursi;
    private int idStudio;
    private String baris;
    private int nomor;

    public Kursi() {
    }

    public Kursi(int idKursi, int idStudio, String baris, int nomor) {
        this.idKursi = idKursi;
        this.idStudio = idStudio;
        this.baris = baris;
        this.nomor = nomor;
    }

    public String getBaris() {
        return baris;
    }

    public int getIdKursi() {
        return idKursi;
    }

    public int getIdStudio() {
        return idStudio;
    }

    public int getNomor() {
        return nomor;
    }

    public void setBaris(String baris) {
        this.baris = baris;
    }

    public void setIdKursi(int idKursi) {
        this.idKursi = idKursi;
    }

    public void setIdStudio(int idStudio) {
        this.idStudio = idStudio;
    }

    public void setNomor(int nomor) {
        this.nomor = nomor;
    }
}
