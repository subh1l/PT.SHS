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
}

create view laporan_view as
select tanggal