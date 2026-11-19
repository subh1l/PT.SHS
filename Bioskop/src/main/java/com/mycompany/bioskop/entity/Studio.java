/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.entity;

/**
 *
 * @author bill
 */
public class Studio {
    private int idStudio;
    private String namaStudio;
    private String tipe;
    private int kapasitas;

    public Studio() {
    }

    public Studio(int idStudio, String namaStudio, String tipe, int kapasitas) {
        this.idStudio = idStudio;
        this.namaStudio = namaStudio;
        this.tipe = tipe;
        this.kapasitas = kapasitas;
    }

    public int getIdStudio() {
        return idStudio;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public String getNamaStudio() {
        return namaStudio;
    }

    public String getTipe() {
        return tipe;
    }

    public void setIdStudio(int idStudio) {
        this.idStudio = idStudio;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    public void setNamaStudio(String namaStudio) {
        this.namaStudio = namaStudio;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
}
