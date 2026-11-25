/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.bioskop.view;

import com.mycompany.bioskop.entity.Film;
import com.mycompany.bioskop.entity.Jadwal;
import com.mycompany.bioskop.entity.Kursi;
import com.mycompany.bioskop.entity.Studio;
import com.mycompany.bioskop.repository.DatabaseUtil;
import com.mycompany.bioskop.repository.FilmRepository;
import com.mycompany.bioskop.repository.StudioRepository;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

/**
 *
 * @author ACER NITRO
 */
public class SeatSelectionPanel extends javax.swing.JPanel {

    private final Jadwal jadwalTerpilih;
    private final int idUser;

    private final Map<String, JToggleButton> tombolKursi = new LinkedHashMap<>();
    private final Map<String, Kursi> dataKursi = new LinkedHashMap<>();
    private final Map<String, Color> warnaAsli = new LinkedHashMap<>();

    private final Color warnaDipilih = new Color(46, 160, 67);
    private final Color warnaTerisi = new Color(150, 150, 150);

    private final NumberFormat formatRupiah
            = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    /**
     * Creates new form SeatSelectionPanel
     */
    public SeatSelectionPanel() {
        this(null, 0);
    }

    public SeatSelectionPanel(Jadwal jadwal, int idUser) {
        this.jadwalTerpilih = jadwal;
        this.idUser = idUser;

        initComponents();
        daftarkanTombolKursi();

        if (jadwal == null) {
            nonaktifkanSemuaKursi();
            btnLanjutPembayaran.setEnabled(false);
            return;
        }

        tampilkanInfoJadwal();
        muatKursi();

        btnKembaliFilm.addActionListener(e -> {
            java.awt.Window window
                    = javax.swing.SwingUtilities.getWindowAncestor(this);

            if (window instanceof UserMainFrame frame) {
                frame.tampilkanBeranda();
            }
        });

        btnLanjutPembayaran.addActionListener(e -> lanjutPembayaran());
    }
    
    private void daftarkanTombolKursi() {
        JToggleButton[][] tombol = {
            {btnF1, btnF2, btnF3, btnF4, btnF5, btnF6, btnF7, btnF8},
            {btnA1, btnA2, btnA3, btnA4, btnA5, btnA6, btnA7, btnA8},
            {btnB1, btnB2, btnB3, btnB4, btnB5, btnB6, btnB7, btnB8},
            {btnC1, btnC2, btnC3, btnC4, btnC5, btnC6, btnC7, btnC8},
            {btnD1, btnD2, btnD3, btnD4, btnD5, btnD6, btnD7, btnD8},
            {btnE1, bbtnE2, btnE3, btnE4, btnE5, btnE6, btnE7, btnE8}
        };

        String[] baris = {"F", "A", "B", "C", "D", "E"};

        for (int i = 0; i < tombol.length; i++) {
            for (int j = 0; j < tombol[i].length; j++) {
                String kode = baris[i] + (j + 1);
                JToggleButton button = tombol[i][j];

                tombolKursi.put(kode, button);
                warnaAsli.put(kode, button.getBackground());

                button.setToolTipText(kode);

                button.addItemListener(e -> {
                    if (button.isSelected()) {
                        button.setBackground(warnaDipilih);
                    } else {
                        button.setBackground(warnaAsli.get(kode));
                    }

                    perbaruiRingkasan();
                });
            }
        }
    }

    private void nonaktifkanSemuaKursi() {
        for (JToggleButton button : tombolKursi.values()) {
            button.setEnabled(false);
        }
    }

    private void tampilkanInfoJadwal() {
        Film film = new FilmRepository().getFilm(jadwalTerpilih.getIdFilm());

        String judulFilm = film.getJudul() == null
                ? "Film tidak ditemukan"
                : film.getJudul();

        String tanggal = jadwalTerpilih.getTanggal() == null
                ? "-"
                : jadwalTerpilih.getTanggal().format(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy",
                                new Locale("id", "ID"))
                );

        String jam = jadwalTerpilih.getJamMulai() == null
                ? "-"
                : jadwalTerpilih.getJamMulai().format(
                        DateTimeFormatter.ofPattern("HH:mm")
                );

        lblInfoFilmKursi.setText(
                judulFilm + " | " + tanggal + " | " + jam + " WIB"
        );

        lblHargaKursi.setText(formatRupiah.format(jadwalTerpilih.getHarga()));
        perbaruiRingkasan();
    }

    private void muatKursi() {
        nonaktifkanSemuaKursi();
        dataKursi.clear();

        Set<Integer> kursiTerisi = new HashSet<>();

        String sqlKursi
                = "SELECT id_kursi, baris, nomor "
                + "FROM kursi WHERE id_studio = ?";

        String sqlTerisi
                = "SELECT DISTINCT d.id_kursi "
                + "FROM detail_pemesanan d "
                + "JOIN pemesanan p ON p.id_pemesanan = d.id_pemesanan "
                + "WHERE d.id_jadwal = ? AND COALESCE(p.status, 'Pending') <> 'Gagal'";

        try (Connection conn = DatabaseUtil.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(sqlKursi)) {
                stmt.setInt(1, jadwalTerpilih.getIdStudio());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Kursi kursi = new Kursi(
                                rs.getInt("id_kursi"),
                                jadwalTerpilih.getIdStudio(),
                                rs.getString("baris").trim().toUpperCase(),
                                rs.getInt("nomor")
                        );

                        String kode = kursi.getBaris() + kursi.getNomor();

                        if (tombolKursi.containsKey(kode)) {
                            dataKursi.put(kode, kursi);
                        }
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlTerisi)) {
                stmt.setInt(1, jadwalTerpilih.getIdJadwal());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        kursiTerisi.add(rs.getInt("id_kursi"));
                    }
                }
            }

            for (Map.Entry<String, JToggleButton> entry : tombolKursi.entrySet()) {
                String kode = entry.getKey();
                JToggleButton button = entry.getValue();
                Kursi kursi = dataKursi.get(kode);

                button.setSelected(false);

                if (kursi == null) {
                    button.setEnabled(false);
                    button.setBackground(warnaTerisi);
                    button.setToolTipText(kode + " - Tidak terdaftar di database");
                } else if (kursiTerisi.contains(kursi.getIdKursi())) {
                    button.setEnabled(false);
                    button.setBackground(warnaTerisi);
                    button.setToolTipText(kode + " - Sudah dipesan");
                } else {
                    button.setEnabled(true);
                    button.setBackground(warnaAsli.get(kode));
                    button.setToolTipText(kode + " - Tersedia");
                }
            }

            if (dataKursi.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data kursi untuk studio ini belum tersedia di database.",
                        "Kursi tidak tersedia",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {
            nonaktifkanSemuaKursi();

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat kursi dari database:\n" + e.getMessage(),
                    "Kesalahan Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        perbaruiRingkasan();
    }

    private List<Kursi> getKursiDipilih() {
        List<Kursi> hasil = new ArrayList<>();

        for (Map.Entry<String, JToggleButton> entry : tombolKursi.entrySet()) {
            if (entry.getValue().isSelected()) {
                Kursi kursi = dataKursi.get(entry.getKey());

                if (kursi != null) {
                    hasil.add(kursi);
                }
            }
        }

        return hasil;
    }

    private void perbaruiRingkasan() {
        List<Kursi> dipilih = getKursiDipilih();

        List<String> kodeKursi = dipilih.stream()
                .map(k -> k.getBaris() + k.getNomor())
                .collect(Collectors.toList());

        int jumlah = dipilih.size();
        double total = jumlah * jadwalTerpilihHarga();

        lblRingkasanKursi.setText(
                kodeKursi.isEmpty() ? "-" : String.join(", ", kodeKursi)
        );

        lblJumlahKursi.setText(String.valueOf(jumlah));
        lblHargaKursi.setText(formatRupiah.format(jadwalTerpilihHarga()));
        lblTotalHargaKursi.setText("Total: " + formatRupiah.format(total));
    }

    private double jadwalTerpilihHarga() {
        return jadwalTerpilih == null ? 0 : jadwalTerpilih.getHarga();
    }

    private void lanjutPembayaran() {
        List<Kursi> dipilih = getKursiDipilih();

        if (dipilih.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih minimal satu kursi terlebih dahulu.",
                    "Kursi belum dipilih",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        java.awt.Window window
                = javax.swing.SwingUtilities.getWindowAncestor(this);

        if (window instanceof UserMainFrame frame) {
            frame.tampilkanPanel(
                    new PembayaranPanel(jadwalTerpilih, idUser, dipilih)
            );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblJudulKursi = new javax.swing.JLabel();
        lblInfoFilmKursi = new javax.swing.JLabel();
        pnlLayarBioskop = new javax.swing.JPanel();
        lblJudulKursi1 = new javax.swing.JLabel();
        pnlRingkasanKursi = new javax.swing.JPanel();
        lblJudulRingkasan = new javax.swing.JLabel();
        lblTotalHargaKursi = new javax.swing.JLabel();
        lblRingkasanKursi = new javax.swing.JLabel();
        lblJumlahKursi = new javax.swing.JLabel();
        lblHargaKursi = new javax.swing.JLabel();
        pnlDenahKursi = new javax.swing.JPanel();
        btnF1 = new javax.swing.JToggleButton();
        btnF8 = new javax.swing.JToggleButton();
        btnF3 = new javax.swing.JToggleButton();
        btnF2 = new javax.swing.JToggleButton();
        btnA1 = new javax.swing.JToggleButton();
        btnB1 = new javax.swing.JToggleButton();
        btnC1 = new javax.swing.JToggleButton();
        btnD1 = new javax.swing.JToggleButton();
        btnE1 = new javax.swing.JToggleButton();
        btnF4 = new javax.swing.JToggleButton();
        btnF5 = new javax.swing.JToggleButton();
        btnF6 = new javax.swing.JToggleButton();
        btnF7 = new javax.swing.JToggleButton();
        btnA8 = new javax.swing.JToggleButton();
        btnB8 = new javax.swing.JToggleButton();
        btnC8 = new javax.swing.JToggleButton();
        btnD8 = new javax.swing.JToggleButton();
        btnE8 = new javax.swing.JToggleButton();
        btnA2 = new javax.swing.JToggleButton();
        btnB2 = new javax.swing.JToggleButton();
        btnC2 = new javax.swing.JToggleButton();
        btnD2 = new javax.swing.JToggleButton();
        bbtnE2 = new javax.swing.JToggleButton();
        btnA3 = new javax.swing.JToggleButton();
        btnB3 = new javax.swing.JToggleButton();
        btnC3 = new javax.swing.JToggleButton();
        btnD3 = new javax.swing.JToggleButton();
        btnE3 = new javax.swing.JToggleButton();
        btnA4 = new javax.swing.JToggleButton();
        btnB4 = new javax.swing.JToggleButton();
        btnC4 = new javax.swing.JToggleButton();
        btnD4 = new javax.swing.JToggleButton();
        btnE4 = new javax.swing.JToggleButton();
        btnA5 = new javax.swing.JToggleButton();
        btnA7 = new javax.swing.JToggleButton();
        btnB7 = new javax.swing.JToggleButton();
        btnC7 = new javax.swing.JToggleButton();
        btnD7 = new javax.swing.JToggleButton();
        btnE7 = new javax.swing.JToggleButton();
        btnA6 = new javax.swing.JToggleButton();
        btnB6 = new javax.swing.JToggleButton();
        btnC6 = new javax.swing.JToggleButton();
        btnD6 = new javax.swing.JToggleButton();
        btnE6 = new javax.swing.JToggleButton();
        btnB5 = new javax.swing.JToggleButton();
        btnC5 = new javax.swing.JToggleButton();
        btnD5 = new javax.swing.JToggleButton();
        btnE5 = new javax.swing.JToggleButton();
        lblJudulKursi2 = new javax.swing.JLabel();
        lblJudulKursi3 = new javax.swing.JLabel();
        lblJudulKursi4 = new javax.swing.JLabel();
        lblJudulKursi5 = new javax.swing.JLabel();
        lblJudulKursi6 = new javax.swing.JLabel();
        lblJudulKursi7 = new javax.swing.JLabel();
        lblKeteranganTersedia = new javax.swing.JLabel();
        lblKeteranganDipilih = new javax.swing.JLabel();
        lblKeteranganTerisi = new javax.swing.JLabel();
        pnlKeteranganTerpilih = new javax.swing.JPanel();
        pnlKeteranganTersedia = new javax.swing.JPanel();
        pnlKeteranganTerisi = new javax.swing.JPanel();
        btnKembaliFilm = new javax.swing.JButton();
        btnLanjutPembayaran = new javax.swing.JButton();

        setBackground(new java.awt.Color(240, 237, 248));

        pnlHeader.setBackground(new java.awt.Color(240, 237, 248));

        lblJudulKursi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulKursi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi.setText("Pilih Kursi");

        lblInfoFilmKursi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblInfoFilmKursi.setForeground(new java.awt.Color(110, 110, 110));
        lblInfoFilmKursi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblInfoFilmKursi.setText("Avengers: Endgame | 20 November 2026 | 19.00 WIB");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblJudulKursi)
                    .addComponent(lblInfoFilmKursi, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(619, Short.MAX_VALUE))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addComponent(lblJudulKursi)
                .addGap(0, 0, 0)
                .addComponent(lblInfoFilmKursi, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlLayarBioskop.setBackground(new java.awt.Color(48, 35, 109));

        lblJudulKursi1.setBackground(new java.awt.Color(255, 255, 255));
        lblJudulKursi1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi1.setForeground(new java.awt.Color(255, 255, 255));
        lblJudulKursi1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi1.setText("Layar Bioskop");

        javax.swing.GroupLayout pnlLayarBioskopLayout = new javax.swing.GroupLayout(pnlLayarBioskop);
        pnlLayarBioskop.setLayout(pnlLayarBioskopLayout);
        pnlLayarBioskopLayout.setHorizontalGroup(
            pnlLayarBioskopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLayarBioskopLayout.createSequentialGroup()
                .addGap(425, 425, 425)
                .addComponent(lblJudulKursi1, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlLayarBioskopLayout.setVerticalGroup(
            pnlLayarBioskopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLayarBioskopLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblJudulKursi1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlRingkasanKursi.setBackground(new java.awt.Color(255, 255, 255));
        pnlRingkasanKursi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 209, 231)));

        lblJudulRingkasan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulRingkasan.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulRingkasan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulRingkasan.setText("Ringkasan Pesanan");

        lblTotalHargaKursi.setBackground(new java.awt.Color(118, 87, 189));
        lblTotalHargaKursi.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTotalHargaKursi.setForeground(new java.awt.Color(118, 87, 189));
        lblTotalHargaKursi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalHargaKursi.setText("Total");

        lblRingkasanKursi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblRingkasanKursi.setForeground(new java.awt.Color(110, 110, 110));
        lblRingkasanKursi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRingkasanKursi.setText("Kursi Dipilih");

        lblJumlahKursi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblJumlahKursi.setForeground(new java.awt.Color(110, 110, 110));
        lblJumlahKursi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJumlahKursi.setText("Jumlah Tiket");

        lblHargaKursi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblHargaKursi.setForeground(new java.awt.Color(110, 110, 110));
        lblHargaKursi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHargaKursi.setText("Harga Per Tiket");

        pnlDenahKursi.setBackground(new java.awt.Color(255, 255, 255));
        pnlDenahKursi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 209, 231)));

        btnF1.setBackground(new java.awt.Color(118, 87, 189));
        btnF1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF1.setForeground(new java.awt.Color(255, 255, 255));
        btnF1.setText("1");
        btnF1.setFocusable(false);
        btnF1.addActionListener(this::btnF1ActionPerformed);

        btnF8.setBackground(new java.awt.Color(118, 87, 189));
        btnF8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF8.setForeground(new java.awt.Color(255, 255, 255));
        btnF8.setText("8");
        btnF8.setFocusable(false);
        btnF8.addActionListener(this::btnF8ActionPerformed);

        btnF3.setBackground(new java.awt.Color(118, 87, 189));
        btnF3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF3.setForeground(new java.awt.Color(255, 255, 255));
        btnF3.setText("3");
        btnF3.setFocusable(false);
        btnF3.addActionListener(this::btnF3ActionPerformed);

        btnF2.setBackground(new java.awt.Color(118, 87, 189));
        btnF2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF2.setForeground(new java.awt.Color(255, 255, 255));
        btnF2.setText("2");
        btnF2.setFocusable(false);
        btnF2.addActionListener(this::btnF2ActionPerformed);

        btnA1.setBackground(new java.awt.Color(118, 87, 189));
        btnA1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA1.setForeground(new java.awt.Color(255, 255, 255));
        btnA1.setText("1");
        btnA1.setFocusable(false);
        btnA1.addActionListener(this::btnA1ActionPerformed);

        btnB1.setBackground(new java.awt.Color(118, 87, 189));
        btnB1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB1.setForeground(new java.awt.Color(255, 255, 255));
        btnB1.setText("1");
        btnB1.setFocusable(false);
        btnB1.addActionListener(this::btnB1ActionPerformed);

        btnC1.setBackground(new java.awt.Color(118, 87, 189));
        btnC1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC1.setForeground(new java.awt.Color(255, 255, 255));
        btnC1.setText("1");
        btnC1.setFocusable(false);
        btnC1.addActionListener(this::btnC1ActionPerformed);

        btnD1.setBackground(new java.awt.Color(118, 87, 189));
        btnD1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD1.setForeground(new java.awt.Color(255, 255, 255));
        btnD1.setText("1");
        btnD1.setFocusable(false);
        btnD1.addActionListener(this::btnD1ActionPerformed);

        btnE1.setBackground(new java.awt.Color(118, 87, 189));
        btnE1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE1.setForeground(new java.awt.Color(255, 255, 255));
        btnE1.setText("1");
        btnE1.setFocusable(false);
        btnE1.addActionListener(this::btnE1ActionPerformed);

        btnF4.setBackground(new java.awt.Color(118, 87, 189));
        btnF4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF4.setForeground(new java.awt.Color(255, 255, 255));
        btnF4.setText("4");
        btnF4.setFocusable(false);
        btnF4.addActionListener(this::btnF4ActionPerformed);

        btnF5.setBackground(new java.awt.Color(118, 87, 189));
        btnF5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF5.setForeground(new java.awt.Color(255, 255, 255));
        btnF5.setText("5");
        btnF5.setFocusable(false);
        btnF5.addActionListener(this::btnF5ActionPerformed);

        btnF6.setBackground(new java.awt.Color(118, 87, 189));
        btnF6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF6.setForeground(new java.awt.Color(255, 255, 255));
        btnF6.setText("6");
        btnF6.setFocusable(false);
        btnF6.addActionListener(this::btnF6ActionPerformed);

        btnF7.setBackground(new java.awt.Color(118, 87, 189));
        btnF7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF7.setForeground(new java.awt.Color(255, 255, 255));
        btnF7.setText("7");
        btnF7.setFocusable(false);
        btnF7.addActionListener(this::btnF7ActionPerformed);

        btnA8.setBackground(new java.awt.Color(118, 87, 189));
        btnA8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA8.setForeground(new java.awt.Color(255, 255, 255));
        btnA8.setText("8");
        btnA8.setFocusable(false);
        btnA8.addActionListener(this::btnA8ActionPerformed);

        btnB8.setBackground(new java.awt.Color(118, 87, 189));
        btnB8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB8.setForeground(new java.awt.Color(255, 255, 255));
        btnB8.setText("8");
        btnB8.setFocusable(false);
        btnB8.addActionListener(this::btnB8ActionPerformed);

        btnC8.setBackground(new java.awt.Color(118, 87, 189));
        btnC8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC8.setForeground(new java.awt.Color(255, 255, 255));
        btnC8.setText("8");
        btnC8.setFocusable(false);
        btnC8.addActionListener(this::btnC8ActionPerformed);

        btnD8.setBackground(new java.awt.Color(118, 87, 189));
        btnD8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD8.setForeground(new java.awt.Color(255, 255, 255));
        btnD8.setText("8");
        btnD8.setFocusable(false);
        btnD8.addActionListener(this::btnD8ActionPerformed);

        btnE8.setBackground(new java.awt.Color(118, 87, 189));
        btnE8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE8.setForeground(new java.awt.Color(255, 255, 255));
        btnE8.setText("8");
        btnE8.setFocusable(false);
        btnE8.addActionListener(this::btnE8ActionPerformed);

        btnA2.setBackground(new java.awt.Color(118, 87, 189));
        btnA2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA2.setForeground(new java.awt.Color(255, 255, 255));
        btnA2.setText("2");
        btnA2.setFocusable(false);
        btnA2.addActionListener(this::btnA2ActionPerformed);

        btnB2.setBackground(new java.awt.Color(118, 87, 189));
        btnB2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB2.setForeground(new java.awt.Color(255, 255, 255));
        btnB2.setText("2");
        btnB2.setFocusable(false);
        btnB2.addActionListener(this::btnB2ActionPerformed);

        btnC2.setBackground(new java.awt.Color(118, 87, 189));
        btnC2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC2.setForeground(new java.awt.Color(255, 255, 255));
        btnC2.setText("2");
        btnC2.setFocusable(false);
        btnC2.addActionListener(this::btnC2ActionPerformed);

        btnD2.setBackground(new java.awt.Color(118, 87, 189));
        btnD2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD2.setForeground(new java.awt.Color(255, 255, 255));
        btnD2.setText("2");
        btnD2.setFocusable(false);
        btnD2.addActionListener(this::btnD2ActionPerformed);

        bbtnE2.setBackground(new java.awt.Color(118, 87, 189));
        bbtnE2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        bbtnE2.setForeground(new java.awt.Color(255, 255, 255));
        bbtnE2.setText("2");
        bbtnE2.setFocusable(false);
        bbtnE2.addActionListener(this::bbtnE2ActionPerformed);

        btnA3.setBackground(new java.awt.Color(118, 87, 189));
        btnA3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA3.setForeground(new java.awt.Color(255, 255, 255));
        btnA3.setText("3");
        btnA3.setFocusable(false);
        btnA3.addActionListener(this::btnA3ActionPerformed);

        btnB3.setBackground(new java.awt.Color(118, 87, 189));
        btnB3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB3.setForeground(new java.awt.Color(255, 255, 255));
        btnB3.setText("3");
        btnB3.setFocusable(false);
        btnB3.addActionListener(this::btnB3ActionPerformed);

        btnC3.setBackground(new java.awt.Color(118, 87, 189));
        btnC3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC3.setForeground(new java.awt.Color(255, 255, 255));
        btnC3.setText("3");
        btnC3.setFocusable(false);
        btnC3.addActionListener(this::btnC3ActionPerformed);

        btnD3.setBackground(new java.awt.Color(118, 87, 189));
        btnD3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD3.setForeground(new java.awt.Color(255, 255, 255));
        btnD3.setText("3");
        btnD3.setFocusable(false);
        btnD3.addActionListener(this::btnD3ActionPerformed);

        btnE3.setBackground(new java.awt.Color(118, 87, 189));
        btnE3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE3.setForeground(new java.awt.Color(255, 255, 255));
        btnE3.setText("3");
        btnE3.setFocusable(false);
        btnE3.addActionListener(this::btnE3ActionPerformed);

        btnA4.setBackground(new java.awt.Color(118, 87, 189));
        btnA4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA4.setForeground(new java.awt.Color(255, 255, 255));
        btnA4.setText("4");
        btnA4.setFocusable(false);
        btnA4.addActionListener(this::btnA4ActionPerformed);

        btnB4.setBackground(new java.awt.Color(118, 87, 189));
        btnB4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB4.setForeground(new java.awt.Color(255, 255, 255));
        btnB4.setText("4");
        btnB4.setFocusable(false);
        btnB4.addActionListener(this::btnB4ActionPerformed);

        btnC4.setBackground(new java.awt.Color(118, 87, 189));
        btnC4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC4.setForeground(new java.awt.Color(255, 255, 255));
        btnC4.setText("4");
        btnC4.setFocusable(false);
        btnC4.addActionListener(this::btnC4ActionPerformed);

        btnD4.setBackground(new java.awt.Color(118, 87, 189));
        btnD4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD4.setForeground(new java.awt.Color(255, 255, 255));
        btnD4.setText("4");
        btnD4.setFocusable(false);
        btnD4.addActionListener(this::btnD4ActionPerformed);

        btnE4.setBackground(new java.awt.Color(118, 87, 189));
        btnE4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE4.setForeground(new java.awt.Color(255, 255, 255));
        btnE4.setText("4");
        btnE4.setFocusable(false);
        btnE4.addActionListener(this::btnE4ActionPerformed);

        btnA5.setBackground(new java.awt.Color(118, 87, 189));
        btnA5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA5.setForeground(new java.awt.Color(255, 255, 255));
        btnA5.setText("5");
        btnA5.setFocusable(false);
        btnA5.addActionListener(this::btnA5ActionPerformed);

        btnA7.setBackground(new java.awt.Color(118, 87, 189));
        btnA7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA7.setForeground(new java.awt.Color(255, 255, 255));
        btnA7.setText("7");
        btnA7.setFocusable(false);
        btnA7.addActionListener(this::btnA7ActionPerformed);

        btnB7.setBackground(new java.awt.Color(118, 87, 189));
        btnB7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB7.setForeground(new java.awt.Color(255, 255, 255));
        btnB7.setText("7");
        btnB7.setFocusable(false);
        btnB7.addActionListener(this::btnB7ActionPerformed);

        btnC7.setBackground(new java.awt.Color(118, 87, 189));
        btnC7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC7.setForeground(new java.awt.Color(255, 255, 255));
        btnC7.setText("7");
        btnC7.setFocusable(false);
        btnC7.addActionListener(this::btnC7ActionPerformed);

        btnD7.setBackground(new java.awt.Color(118, 87, 189));
        btnD7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD7.setForeground(new java.awt.Color(255, 255, 255));
        btnD7.setText("7");
        btnD7.setFocusable(false);
        btnD7.addActionListener(this::btnD7ActionPerformed);

        btnE7.setBackground(new java.awt.Color(118, 87, 189));
        btnE7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE7.setForeground(new java.awt.Color(255, 255, 255));
        btnE7.setText("7");
        btnE7.setFocusable(false);
        btnE7.addActionListener(this::btnE7ActionPerformed);

        btnA6.setBackground(new java.awt.Color(118, 87, 189));
        btnA6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA6.setForeground(new java.awt.Color(255, 255, 255));
        btnA6.setText("6");
        btnA6.setFocusable(false);
        btnA6.addActionListener(this::btnA6ActionPerformed);

        btnB6.setBackground(new java.awt.Color(118, 87, 189));
        btnB6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB6.setForeground(new java.awt.Color(255, 255, 255));
        btnB6.setText("6");
        btnB6.setFocusable(false);
        btnB6.addActionListener(this::btnB6ActionPerformed);

        btnC6.setBackground(new java.awt.Color(118, 87, 189));
        btnC6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC6.setForeground(new java.awt.Color(255, 255, 255));
        btnC6.setText("6");
        btnC6.setFocusable(false);
        btnC6.addActionListener(this::btnC6ActionPerformed);

        btnD6.setBackground(new java.awt.Color(118, 87, 189));
        btnD6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD6.setForeground(new java.awt.Color(255, 255, 255));
        btnD6.setText("6");
        btnD6.setFocusable(false);
        btnD6.addActionListener(this::btnD6ActionPerformed);

        btnE6.setBackground(new java.awt.Color(118, 87, 189));
        btnE6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE6.setForeground(new java.awt.Color(255, 255, 255));
        btnE6.setText("6");
        btnE6.setFocusable(false);
        btnE6.addActionListener(this::btnE6ActionPerformed);

        btnB5.setBackground(new java.awt.Color(118, 87, 189));
        btnB5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB5.setForeground(new java.awt.Color(255, 255, 255));
        btnB5.setText("5");
        btnB5.setFocusable(false);
        btnB5.addActionListener(this::btnB5ActionPerformed);

        btnC5.setBackground(new java.awt.Color(118, 87, 189));
        btnC5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC5.setForeground(new java.awt.Color(255, 255, 255));
        btnC5.setText("5");
        btnC5.setFocusable(false);
        btnC5.addActionListener(this::btnC5ActionPerformed);

        btnD5.setBackground(new java.awt.Color(150, 150, 150));
        btnD5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD5.setForeground(new java.awt.Color(255, 255, 255));
        btnD5.setText("5");
        btnD5.setEnabled(false);
        btnD5.setFocusable(false);
        btnD5.addActionListener(this::btnD5ActionPerformed);

        btnE5.setBackground(new java.awt.Color(118, 87, 189));
        btnE5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE5.setForeground(new java.awt.Color(255, 255, 255));
        btnE5.setText("5");
        btnE5.setFocusable(false);
        btnE5.addActionListener(this::btnE5ActionPerformed);

        lblJudulKursi2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi2.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulKursi2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi2.setText("F");

        lblJudulKursi3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi3.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulKursi3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi3.setText("A");

        lblJudulKursi4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi4.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulKursi4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi4.setText("B");

        lblJudulKursi5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi5.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulKursi5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi5.setText("C");

        lblJudulKursi6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi6.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulKursi6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi6.setText("D");

        lblJudulKursi7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulKursi7.setForeground(new java.awt.Color(48, 35, 109));
        lblJudulKursi7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulKursi7.setText("E");

        lblKeteranganTersedia.setBackground(new java.awt.Color(118, 87, 189));
        lblKeteranganTersedia.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKeteranganTersedia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblKeteranganTersedia.setText("Tersedia");

        lblKeteranganDipilih.setBackground(new java.awt.Color(118, 87, 189));
        lblKeteranganDipilih.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKeteranganDipilih.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblKeteranganDipilih.setText("Dipilih");

        lblKeteranganTerisi.setBackground(new java.awt.Color(0, 0, 0));
        lblKeteranganTerisi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKeteranganTerisi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblKeteranganTerisi.setText("Terisi");

        pnlKeteranganTerpilih.setBackground(new java.awt.Color(54, 162, 105));

        javax.swing.GroupLayout pnlKeteranganTerpilihLayout = new javax.swing.GroupLayout(pnlKeteranganTerpilih);
        pnlKeteranganTerpilih.setLayout(pnlKeteranganTerpilihLayout);
        pnlKeteranganTerpilihLayout.setHorizontalGroup(
            pnlKeteranganTerpilihLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        pnlKeteranganTerpilihLayout.setVerticalGroup(
            pnlKeteranganTerpilihLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        pnlKeteranganTersedia.setBackground(new java.awt.Color(118, 87, 189));

        javax.swing.GroupLayout pnlKeteranganTersediaLayout = new javax.swing.GroupLayout(pnlKeteranganTersedia);
        pnlKeteranganTersedia.setLayout(pnlKeteranganTersediaLayout);
        pnlKeteranganTersediaLayout.setHorizontalGroup(
            pnlKeteranganTersediaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        pnlKeteranganTersediaLayout.setVerticalGroup(
            pnlKeteranganTersediaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        pnlKeteranganTerisi.setBackground(new java.awt.Color(150, 150, 150));

        javax.swing.GroupLayout pnlKeteranganTerisiLayout = new javax.swing.GroupLayout(pnlKeteranganTerisi);
        pnlKeteranganTerisi.setLayout(pnlKeteranganTerisiLayout);
        pnlKeteranganTerisiLayout.setHorizontalGroup(
            pnlKeteranganTerisiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        pnlKeteranganTerisiLayout.setVerticalGroup(
            pnlKeteranganTerisiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlDenahKursiLayout = new javax.swing.GroupLayout(pnlDenahKursi);
        pnlDenahKursi.setLayout(pnlDenahKursiLayout);
        pnlDenahKursiLayout.setHorizontalGroup(
            pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblJudulKursi3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnA1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnA2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnA3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnA4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)
                        .addComponent(btnA5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnA6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnA7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnA8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblJudulKursi4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnB1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnB2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnB3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnB4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)
                        .addComponent(btnB5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnB6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnB7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnB8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblJudulKursi5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnC1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnC2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnC3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnC4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)
                        .addComponent(btnC5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnC6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnC7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnC8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblJudulKursi6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnD1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnD2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnD3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnD4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)
                        .addComponent(btnD5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnD6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnD7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnD8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblJudulKursi7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnE1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(bbtnE2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnE3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnE4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)
                        .addComponent(btnE5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnE6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnE7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnE8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblJudulKursi2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnF1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnF2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnF3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnF4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)
                        .addComponent(btnF5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnF6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnF7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnF8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(329, 329, 329)
                        .addComponent(pnlKeteranganTersedia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblKeteranganTersedia)
                        .addGap(43, 43, 43)
                        .addComponent(pnlKeteranganTerpilih, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblKeteranganDipilih)
                        .addGap(34, 34, 34)
                        .addComponent(pnlKeteranganTerisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblKeteranganTerisi)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDenahKursiLayout.setVerticalGroup(
            pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblJudulKursi3))
                    .addComponent(btnA1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnA2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnA3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnA4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnA5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnA6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnA7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnA8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblJudulKursi4))
                    .addComponent(btnB1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnB2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnB3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnB4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnB5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnB6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnB7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnB8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblJudulKursi5))
                    .addComponent(btnC1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnC2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnC3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnC4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnC5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnC6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnC7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnC8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblJudulKursi6))
                    .addComponent(btnD1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnD2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnD3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnD4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnD5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnD6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnD7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnD8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblJudulKursi7))
                    .addComponent(btnE1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bbtnE2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnE3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnE4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnE5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnE6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnE7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnE8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblJudulKursi2))
                    .addComponent(btnF1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnF2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnF3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnF4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnF5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnF6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnF7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnF8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblKeteranganTersedia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKeteranganDipilih, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKeteranganTerisi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlDenahKursiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnlDenahKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlKeteranganTersedia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlKeteranganTerpilih, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlKeteranganTerisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        javax.swing.GroupLayout pnlRingkasanKursiLayout = new javax.swing.GroupLayout(pnlRingkasanKursi);
        pnlRingkasanKursi.setLayout(pnlRingkasanKursiLayout);
        pnlRingkasanKursiLayout.setHorizontalGroup(
            pnlRingkasanKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRingkasanKursiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRingkasanKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRingkasanKursiLayout.createSequentialGroup()
                        .addGroup(pnlRingkasanKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblJudulRingkasan, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlRingkasanKursiLayout.createSequentialGroup()
                                .addComponent(lblRingkasanKursi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblHargaKursi)))
                        .addGap(345, 345, 345))
                    .addGroup(pnlRingkasanKursiLayout.createSequentialGroup()
                        .addComponent(lblJumlahKursi)
                        .addGap(497, 497, 497)
                        .addComponent(lblTotalHargaKursi, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(pnlDenahKursi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlRingkasanKursiLayout.setVerticalGroup(
            pnlRingkasanKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRingkasanKursiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDenahKursi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblJudulRingkasan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(pnlRingkasanKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHargaKursi)
                    .addComponent(lblRingkasanKursi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRingkasanKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalHargaKursi, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblJumlahKursi))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        btnKembaliFilm.setBackground(new java.awt.Color(216, 209, 231));
        btnKembaliFilm.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnKembaliFilm.setForeground(new java.awt.Color(48, 35, 109));
        btnKembaliFilm.setText("Kembali");
        btnKembaliFilm.setFocusable(false);
        btnKembaliFilm.addActionListener(this::btnKembaliFilmActionPerformed);

        btnLanjutPembayaran.setBackground(new java.awt.Color(118, 87, 189));
        btnLanjutPembayaran.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLanjutPembayaran.setForeground(new java.awt.Color(255, 255, 255));
        btnLanjutPembayaran.setText("Lanjut Pembayaran");
        btnLanjutPembayaran.setFocusable(false);
        btnLanjutPembayaran.addActionListener(this::btnLanjutPembayaranActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnKembaliFilm, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLanjutPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(pnlRingkasanKursi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlLayarBioskop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlLayarBioskop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(pnlRingkasanKursi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLanjutPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnKembaliFilm, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF2ActionPerformed

    private void btnF3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF3ActionPerformed

    private void btnF8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF8ActionPerformed

    private void btnF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF1ActionPerformed

    private void btnA1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA1ActionPerformed

    private void btnB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB1ActionPerformed

    private void btnC1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC1ActionPerformed

    private void btnD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD1ActionPerformed

    private void btnE1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnE1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnE1ActionPerformed

    private void btnF4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF4ActionPerformed

    private void btnF5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF5ActionPerformed

    private void btnF6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF6ActionPerformed

    private void btnF7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnF7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnF7ActionPerformed

    private void btnA8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA8ActionPerformed

    private void btnB8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB8ActionPerformed

    private void btnC8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC8ActionPerformed

    private void btnD8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD8ActionPerformed

    private void btnE8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnE8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnE8ActionPerformed

    private void btnA2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA2ActionPerformed

    private void btnB2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB2ActionPerformed

    private void btnC2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC2ActionPerformed

    private void btnD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD2ActionPerformed

    private void bbtnE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bbtnE2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bbtnE2ActionPerformed

    private void btnA3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA3ActionPerformed

    private void btnB3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB3ActionPerformed

    private void btnC3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC3ActionPerformed

    private void btnD3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD3ActionPerformed

    private void btnE3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnE3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnE3ActionPerformed

    private void btnA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA4ActionPerformed

    private void btnB4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB4ActionPerformed

    private void btnC4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC4ActionPerformed

    private void btnD4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD4ActionPerformed

    private void btnE4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnE4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnE4ActionPerformed

    private void btnA5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA5ActionPerformed

    private void btnA7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA7ActionPerformed

    private void btnB7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB7ActionPerformed

    private void btnC7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC7ActionPerformed

    private void btnD7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD7ActionPerformed

    private void btnE7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnE7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnE7ActionPerformed

    private void btnA6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnA6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnA6ActionPerformed

    private void btnB6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB6ActionPerformed

    private void btnC6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC6ActionPerformed

    private void btnD6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD6ActionPerformed

    private void btnE6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnE6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnE6ActionPerformed

    private void btnB5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnB5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnB5ActionPerformed

    private void btnC5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnC5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnC5ActionPerformed

    private void btnD5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnD5ActionPerformed

    private void btnE5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnE5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnE5ActionPerformed

    private void btnKembaliFilmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliFilmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliFilmActionPerformed

    private void btnLanjutPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLanjutPembayaranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLanjutPembayaranActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton bbtnE2;
    private javax.swing.JToggleButton btnA1;
    private javax.swing.JToggleButton btnA2;
    private javax.swing.JToggleButton btnA3;
    private javax.swing.JToggleButton btnA4;
    private javax.swing.JToggleButton btnA5;
    private javax.swing.JToggleButton btnA6;
    private javax.swing.JToggleButton btnA7;
    private javax.swing.JToggleButton btnA8;
    private javax.swing.JToggleButton btnB1;
    private javax.swing.JToggleButton btnB2;
    private javax.swing.JToggleButton btnB3;
    private javax.swing.JToggleButton btnB4;
    private javax.swing.JToggleButton btnB5;
    private javax.swing.JToggleButton btnB6;
    private javax.swing.JToggleButton btnB7;
    private javax.swing.JToggleButton btnB8;
    private javax.swing.JToggleButton btnC1;
    private javax.swing.JToggleButton btnC2;
    private javax.swing.JToggleButton btnC3;
    private javax.swing.JToggleButton btnC4;
    private javax.swing.JToggleButton btnC5;
    private javax.swing.JToggleButton btnC6;
    private javax.swing.JToggleButton btnC7;
    private javax.swing.JToggleButton btnC8;
    private javax.swing.JToggleButton btnD1;
    private javax.swing.JToggleButton btnD2;
    private javax.swing.JToggleButton btnD3;
    private javax.swing.JToggleButton btnD4;
    private javax.swing.JToggleButton btnD5;
    private javax.swing.JToggleButton btnD6;
    private javax.swing.JToggleButton btnD7;
    private javax.swing.JToggleButton btnD8;
    private javax.swing.JToggleButton btnE1;
    private javax.swing.JToggleButton btnE3;
    private javax.swing.JToggleButton btnE4;
    private javax.swing.JToggleButton btnE5;
    private javax.swing.JToggleButton btnE6;
    private javax.swing.JToggleButton btnE7;
    private javax.swing.JToggleButton btnE8;
    private javax.swing.JToggleButton btnF1;
    private javax.swing.JToggleButton btnF2;
    private javax.swing.JToggleButton btnF3;
    private javax.swing.JToggleButton btnF4;
    private javax.swing.JToggleButton btnF5;
    private javax.swing.JToggleButton btnF6;
    private javax.swing.JToggleButton btnF7;
    private javax.swing.JToggleButton btnF8;
    private javax.swing.JButton btnKembaliFilm;
    private javax.swing.JButton btnLanjutPembayaran;
    private javax.swing.JLabel lblHargaKursi;
    private javax.swing.JLabel lblInfoFilmKursi;
    private javax.swing.JLabel lblJudulKursi;
    private javax.swing.JLabel lblJudulKursi1;
    private javax.swing.JLabel lblJudulKursi2;
    private javax.swing.JLabel lblJudulKursi3;
    private javax.swing.JLabel lblJudulKursi4;
    private javax.swing.JLabel lblJudulKursi5;
    private javax.swing.JLabel lblJudulKursi6;
    private javax.swing.JLabel lblJudulKursi7;
    private javax.swing.JLabel lblJudulRingkasan;
    private javax.swing.JLabel lblJumlahKursi;
    private javax.swing.JLabel lblKeteranganDipilih;
    private javax.swing.JLabel lblKeteranganTerisi;
    private javax.swing.JLabel lblKeteranganTersedia;
    private javax.swing.JLabel lblRingkasanKursi;
    private javax.swing.JLabel lblTotalHargaKursi;
    private javax.swing.JPanel pnlDenahKursi;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlKeteranganTerisi;
    private javax.swing.JPanel pnlKeteranganTerpilih;
    private javax.swing.JPanel pnlKeteranganTersedia;
    private javax.swing.JPanel pnlLayarBioskop;
    private javax.swing.JPanel pnlRingkasanKursi;
    // End of variables declaration//GEN-END:variables
}
