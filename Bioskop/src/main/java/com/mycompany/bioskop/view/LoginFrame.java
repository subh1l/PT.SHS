
package com.mycompany.bioskop.view;

import com.mycompany.bioskop.repository.Databaseutil;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {

        setTitle("Login - PT.SHS Bioskop");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel lblJudul = new JLabel("PT.SHS BIOSKOP");
        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        lblJudul.setBounds(85, 25, 250, 35);
        panel.add(lblJudul);

        JLabel lblSubjudul = new JLabel("Silakan masuk ke akun Anda");
        lblSubjudul.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubjudul.setBounds(105, 65, 220, 25);
        panel.add(lblSubjudul);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(50, 110, 290, 25);
        panel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(50, 135, 290, 35);
        panel.add(txtEmail);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(50, 180, 290, 25);
        panel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 205, 290, 35);
        panel.add(txtPassword);

        btnLogin = new JButton("LOGIN");
        btnLogin.setBounds(50, 260, 290, 35);
        btnLogin.setBackground(new Color(30, 50, 90));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> prosesLogin());

        getRootPane().setDefaultButton(btnLogin);

        add(panel);
    }

    private void prosesLogin() {

        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Email dan password harus diisi!"
            );
            return;
        }

        String sql = "SELECT id_user, nama, email, role "
                   + "FROM user WHERE email = ? AND password = ?";

        try (
            Connection conn = Databaseutil.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {

            pst.setString(1, email);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    String nama = rs.getString("nama");
                    String role = rs.getString("role");

                    if ("admin".equalsIgnoreCase(role)) {

                        JOptionPane.showMessageDialog(
                            this,
                            "Selamat datang, " + nama + "!"
                        );

                        new AdminMainFrame().setVisible(true);

                    } else if ("user".equalsIgnoreCase(role)) {

                        JOptionPane.showMessageDialog(
                            this,
                            "Selamat datang, " + nama + "!"
                        );

                        new UserMainFrame().setVisible(true);

                    } else {

                        JOptionPane.showMessageDialog(
                            this,
                            "Role akun tidak dikenali!"
                        );
                        return;
                    }

                    dispose();

                } else {

                    JOptionPane.showMessageDialog(
                        this,
                        "Email atau password salah!"
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Login gagal: " + ex.getMessage()
            );
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
