/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.bioskop.entity;

/**
 *
 * @author bill
 */
public enum FilmStatus {
    TAYANG("Tayang"),
    SEGERA("Segera");
    
    private final String value;
    
    FilmStatus(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public static FilmStatus StatusFromDb(String value){
        for(FilmStatus s : values()){
            if(s.value.equals(value)){
                return s;
            }
        }
        throw new IllegalArgumentException("unknown film status: " + value);
    }
}
