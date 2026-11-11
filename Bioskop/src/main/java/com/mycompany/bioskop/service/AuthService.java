/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bioskop.service;

import com.mycompany.bioskop.exception.AuthException;
import com.mycompany.bioskop.model.User;
import com.mycompany.bioskop.repository.UserRepository;
import com.mycompany.bioskop.util.PasswordHasher;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Pattern;

public class AuthService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String nama, String email, String password, String konfirmasi)
            throws AuthException, SQLException {

        nama = nama == null ? "" : nama.trim();
        email = email == null ? "" : email.trim().toLowerCase();

        if (nama.isEmpty()) {
            throw new AuthException("Nama tidak boleh kosong.");
        }
        if (nama.length() > 100) {
            throw new AuthException("Nama maksimal 100 karakter.");
        }
        if (email.isEmpty()) {
            throw new AuthException("Email tidak boleh kosong.");
        }
        if (email.length() > 100 || !EMAIL.matcher(email).matches()) {
            throw new AuthException("Format email tidak valid.");
        }
        if (password == null || password.length() < 6) {
            throw new AuthException("Password minimal 6 karakter.");
        }
        if (!password.equals(konfirmasi)) {
            throw new AuthException("Konfirmasi password tidak cocok.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new AuthException("Email sudah terdaftar.");
        }

        User baru = new User(0, nama, email, PasswordHasher.hash(password), "user");
        try {
            userRepository.save(baru);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AuthException("Email sudah terdaftar.");
        }
        return baru;
    }
}
