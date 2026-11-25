/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.bioskop.view;

import com.mycompany.bioskop.entity.Film;
import com.mycompany.bioskop.entity.Jadwal;
import com.mycompany.bioskop.entity.Kursi;
import com.mycompany.bioskop.entity.Studio;
import com.mycompany.bioskop.repository.FilmRepository;
import com.mycompany.bioskop.repository.StudioRepository;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 *
 * @author ACER NITRO
 */
public class PembayaranPanel extends javax.swing.JPanel {

    private Jadwal jadwal;
    private int idUser;
    private List<Kursi> kursiDipilih;

    private final javax.swing.ButtonGroup grupMetode
            = new javax.swing.ButtonGroup();
    /**
     * Creates new form PembayaranPanel
     */
    public PembayaranPanel() {
        initComponents();

        grupMetode.add(rbQRIS);
        grupMetode.add(rbTransfer);
        grupMetode.add(rbKasir);
    }

    public PembayaranPanel(Jadwal jadwal, int idUser, List<Kursi> kursiDipilih) {
        this.jadwal = jadwal;
        this.idUser = idUser;
        this.kursiDipilih = new java.util.ArrayList<>(kursiDipilih);

        initComponents();

        grupMetode.add(rbQRIS);
        grupMetode.add(rbTransfer);
        grupMetode.add(rbKasir);

        Film film = new FilmRepository().getFilm(jadwal.getIdFilm());
        Studio studio = new StudioRepository().getStudio(jadwal.getIdStudio());

        NumberFormat rupiah
                = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        String judul = film.getJudul() == null
                ? "Film tidak ditemukan" : film.getJudul();

        String genre = film.getGenre() == null
                ? "-" : film.getGenre();

        String namaStudio = studio.getNamaStudio() == null
                ? "-" : studio.getNamaStudio();

        String tanggal = jadwal.getTanggal() == null
                ? "-" : jadwal.getTanggal().format(
                        DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy", new Locale("id", "ID")));

        String jam = jadwal.getJamMulai() == null
                ? "-" : jadwal.getJamMulai().format(
                        DateTimeFormatter.ofPattern("HH:mm"));

        String daftarKursi = kursiDipilih.stream()
                .map(k -> k.getBaris() + k.getNomor())
                .collect(Collectors.joining(", "));

        int jumlahTiket = kursiDipilih.size();
        double total = jumlahTiket * jadwal.getHarga();

        lblJudulFilm.setText(judul);
        lblGenre.setText(genre);
        lblTanggalPembayaran.setText(tanggal);
        lblJamPembayaran.setText(jam);
        lblStudioPembayaran.setText(namaStudio);
        lblKursiPembayaran.setText(daftarKursi);
        lblJumlahTiket.setText(String.valueOf(jumlahTiket));
        lblHargaPerTiket.setText(rupiah.format(jadwal.getHarga()));
        lblBanyakTiket.setText(jumlahTiket + " tiket");
        lblTotalPembayaran.setText("Total:" + rupiah.format(total));
        lblTotal.setText(rupiah.format(total));
    }
    
    private String getMetodePembayaran() {
        javax.swing.JRadioButton terpilih = null;

        if (rbQRIS.isSelected()) {
            terpilih = rbQRIS;
        } else if (rbTransfer.isSelected()) {
            terpilih = rbTransfer;
        } else if (rbKasir.isSelected()) {
            terpilih = rbKasir;
        }

        if (terpilih == null) {
            return null;
        }

        String metode = terpilih.getText().trim();

        if (!metode.equals("Dana")
                && !metode.equals("GoPay")
                && !metode.equals("Bank")) {
            return null;
        }

        return metode;
    }
    
    private void simpanPemesanan() {
        if (jadwal == null || kursiDipilih == null || kursiDipilih.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Data pemesanan tidak valid.",
                    "Pemesanan gagal",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (idUser <= 0) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "User tidak valid. Silakan login kembali.",
                    "Pemesanan gagal",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String metode = getMetodePembayaran();

        if (metode == null) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Pilih metode pembayaran yang sesuai dengan database.",
                    "Metode belum dipilih",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double harga = jadwal.getHarga();
        double total = harga * kursiDipilih.size();

        String sqlPemesanan
                = "INSERT INTO pemesanan "
                + "(id_user, id_jadwal, tanggal_pesan, total_harga, status) "
                + "VALUES (?, ?, ?, ?, 'Sukses')";

        String sqlDetail
                = "INSERT INTO detail_pemesanan "
                + "(id_pemesanan, id_jadwal, id_kursi, harga) "
                + "VALUES (?, ?, ?, ?)";

        String sqlPembayaran
                = "INSERT INTO pembayaran "
                + "(id_pemesanan, metode, jumlah, tanggal_bayar, status) "
                + "VALUES (?, ?, ?, ?, 'Sukses')";

        try (java.sql.Connection conn
                = com.mycompany.bioskop.repository.DatabaseUtil.getConnection()) {

            conn.setAutoCommit(false);

            try {
                int idPemesanan;

                try (java.sql.PreparedStatement stmt
                        = conn.prepareStatement(
                                sqlPemesanan,
                                java.sql.Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setInt(1, idUser);
                    stmt.setInt(2, jadwal.getIdJadwal());
                    stmt.setObject(3, java.time.LocalDateTime.now());
                    stmt.setDouble(4, total);

                    stmt.executeUpdate();

                    try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new java.sql.SQLException(
                                    "ID pemesanan gagal dibuat."
                            );
                        }

                        idPemesanan = rs.getInt(1);
                    }
                }

                try (java.sql.PreparedStatement stmt
                        = conn.prepareStatement(sqlDetail)) {

                    for (Kursi kursi : kursiDipilih) {
                        stmt.setInt(1, idPemesanan);
                        stmt.setInt(2, jadwal.getIdJadwal());
                        stmt.setInt(3, kursi.getIdKursi());
                        stmt.setDouble(4, harga);
                        stmt.addBatch();
                    }

                    stmt.executeBatch();
                }

                try (java.sql.PreparedStatement stmt
                        = conn.prepareStatement(sqlPembayaran)) {

                    stmt.setInt(1, idPemesanan);
                    stmt.setString(2, metode);
                    stmt.setDouble(3, total);
                    stmt.setObject(4, java.time.LocalDateTime.now());

                    stmt.executeUpdate();
                }

                conn.commit();

                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Pemesanan berhasil!\n"
                        + "ID Pemesanan: " + idPemesanan
                        + "\nTotal: " + NumberFormat
                                .getCurrencyInstance(new Locale("id", "ID"))
                                .format(total),
                        "Pemesanan berhasil",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                );

                java.awt.Window window
                        = javax.swing.SwingUtilities.getWindowAncestor(this);

                if (window instanceof UserMainFrame frame) {
                    frame.tampilkanPanel(new RiwayatPanel(idUser));
                }

            } catch (java.sql.SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Pemesanan gagal disimpan.\n"
                    + "Kemungkinan kursi sudah dipesan atau database bermasalah.\n\n"
                    + e.getMessage(),
                    "Kesalahan Pemesanan",
                    javax.swing.JOptionPane.ERROR_MESSAGE
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
        lblHeader = new javax.swing.JLabel();
        lblHeader1 = new javax.swing.JLabel();
        pnlRingkasan = new javax.swing.JPanel();
        lblJudulFilm = new javax.swing.JLabel();
        lblTotalPembayaran = new javax.swing.JLabel();
        lblTanggalPembayaran = new javax.swing.JLabel();
        lblGenre = new javax.swing.JLabel();
        lblHargaPerTiket = new javax.swing.JLabel();
        lblStudioPembayaran = new javax.swing.JLabel();
        lblKursiPembayaran = new javax.swing.JLabel();
        lblJumlahTiket = new javax.swing.JLabel();
        lblRingkasan = new javax.swing.JLabel();
        lblJamPembayaran = new javax.swing.JLabel();
        pnlMetodeBayar = new javax.swing.JPanel();
        lblMetodeBayar = new javax.swing.JLabel();
        lblDescKasir = new javax.swing.JLabel();
        lblSubMetodeBayar = new javax.swing.JLabel();
        lblDescTransfer = new javax.swing.JLabel();
        rbQRIS = new javax.swing.JRadioButton();
        rbTransfer = new javax.swing.JRadioButton();
        rbKasir = new javax.swing.JRadioButton();
        lblDescQRIS = new javax.swing.JLabel();
        pnlDetailBayar = new javax.swing.JPanel();
        lblDetailMetode = new javax.swing.JLabel();
        lblPetunjukBayar = new javax.swing.JLabel();
        pnlTotalBayar = new javax.swing.JPanel();
        lblTotalBayar = new javax.swing.JLabel();
        lblBanyakTiket = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnBayar = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();

        setBackground(new java.awt.Color(240, 237, 248));

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHeader.setForeground(new java.awt.Color(48, 35, 109));
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHeader.setText("Pembayaran");

        lblHeader1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblHeader1.setForeground(new java.awt.Color(110, 110, 110));
        lblHeader1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHeader1.setText("Selesaikan pembayaran untuk mendapatkan tiket bioskop.");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHeader)
                    .addComponent(lblHeader1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(619, Short.MAX_VALUE))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblHeader)
                .addGap(0, 0, 0)
                .addComponent(lblHeader1)
                .addGap(26, 26, 26))
        );

        pnlRingkasan.setBackground(new java.awt.Color(255, 255, 255));
        pnlRingkasan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 209, 231)));

        lblJudulFilm.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblJudulFilm.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJudulFilm.setText("Judul Film");

        lblTotalPembayaran.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalPembayaran.setForeground(new java.awt.Color(48, 35, 109));
        lblTotalPembayaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalPembayaran.setText("Total:");

        lblTanggalPembayaran.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTanggalPembayaran.setForeground(new java.awt.Color(110, 110, 110));
        lblTanggalPembayaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTanggalPembayaran.setText("Tanggal");

        lblGenre.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblGenre.setForeground(new java.awt.Color(110, 110, 110));
        lblGenre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblGenre.setText("Genre");

        lblHargaPerTiket.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblHargaPerTiket.setForeground(new java.awt.Color(110, 110, 110));
        lblHargaPerTiket.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHargaPerTiket.setText("Harga");

        lblStudioPembayaran.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblStudioPembayaran.setForeground(new java.awt.Color(110, 110, 110));
        lblStudioPembayaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStudioPembayaran.setText("Studio");

        lblKursiPembayaran.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblKursiPembayaran.setForeground(new java.awt.Color(110, 110, 110));
        lblKursiPembayaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblKursiPembayaran.setText("Kursi");

        lblJumlahTiket.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblJumlahTiket.setForeground(new java.awt.Color(110, 110, 110));
        lblJumlahTiket.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJumlahTiket.setText("Jumlah");

        lblRingkasan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblRingkasan.setForeground(new java.awt.Color(48, 35, 109));
        lblRingkasan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRingkasan.setText("Ringkasan Pesanan");

        lblJamPembayaran.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblJamPembayaran.setForeground(new java.awt.Color(110, 110, 110));
        lblJamPembayaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJamPembayaran.setText("Jam");

        javax.swing.GroupLayout pnlRingkasanLayout = new javax.swing.GroupLayout(pnlRingkasan);
        pnlRingkasan.setLayout(pnlRingkasanLayout);
        pnlRingkasanLayout.setHorizontalGroup(
            pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRingkasanLayout.createSequentialGroup()
                .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRingkasanLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(lblRingkasan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlRingkasanLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblJudulFilm))
                    .addGroup(pnlRingkasanLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRingkasanLayout.createSequentialGroup()
                                    .addComponent(lblGenre, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(250, 250, 250))
                                .addGroup(pnlRingkasanLayout.createSequentialGroup()
                                    .addComponent(lblTanggalPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(230, 230, 230)))
                            .addGroup(pnlRingkasanLayout.createSequentialGroup()
                                .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblJamPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblStudioPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(240, 240, 240)))
                        .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotalPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblKursiPembayaran)
                            .addComponent(lblHargaPerTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblJumlahTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        pnlRingkasanLayout.setVerticalGroup(
            pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRingkasanLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(lblRingkasan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblJudulFilm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGenre)
                    .addComponent(lblKursiPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTanggalPembayaran)
                    .addComponent(lblHargaPerTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJumlahTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblJamPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRingkasanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStudioPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalPembayaran))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        pnlMetodeBayar.setBackground(new java.awt.Color(255, 255, 255));
        pnlMetodeBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 209, 231)));

        lblMetodeBayar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMetodeBayar.setForeground(new java.awt.Color(48, 35, 109));
        lblMetodeBayar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMetodeBayar.setText("Metode Pembayaran");

        lblDescKasir.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblDescKasir.setForeground(new java.awt.Color(110, 110, 110));
        lblDescKasir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDescKasir.setText("Pembayaran melalui E-Wallet GoPay");

        lblSubMetodeBayar.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSubMetodeBayar.setForeground(new java.awt.Color(110, 110, 110));
        lblSubMetodeBayar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSubMetodeBayar.setText("Pilih metode pembayaran");

        lblDescTransfer.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblDescTransfer.setForeground(new java.awt.Color(110, 110, 110));
        lblDescTransfer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDescTransfer.setText("Transfer melalui rekening bank");

        rbQRIS.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        rbQRIS.setText("Dana");
        rbQRIS.addActionListener(this::rbQRISActionPerformed);

        rbTransfer.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        rbTransfer.setText("Bank");

        rbKasir.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        rbKasir.setText("GoPay");
        rbKasir.addActionListener(this::rbKasirActionPerformed);

        lblDescQRIS.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblDescQRIS.setForeground(new java.awt.Color(110, 110, 110));
        lblDescQRIS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDescQRIS.setText("Pembayaran melalui E-Wallet Dana");

        javax.swing.GroupLayout pnlMetodeBayarLayout = new javax.swing.GroupLayout(pnlMetodeBayar);
        pnlMetodeBayar.setLayout(pnlMetodeBayarLayout);
        pnlMetodeBayarLayout.setHorizontalGroup(
            pnlMetodeBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMetodeBayarLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(pnlMetodeBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMetodeBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubMetodeBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbQRIS)
                    .addComponent(rbTransfer)
                    .addComponent(rbKasir)
                    .addGroup(pnlMetodeBayarLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(pnlMetodeBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDescQRIS, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDescTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDescKasir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlMetodeBayarLayout.setVerticalGroup(
            pnlMetodeBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMetodeBayarLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(lblMetodeBayar)
                .addGap(0, 0, 0)
                .addComponent(lblSubMetodeBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(pnlMetodeBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbQRIS)
                    .addGroup(pnlMetodeBayarLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblDescQRIS, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addGroup(pnlMetodeBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMetodeBayarLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblDescTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rbTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlMetodeBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMetodeBayarLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblDescKasir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rbKasir)))
        );

        pnlDetailBayar.setBackground(new java.awt.Color(255, 255, 255));
        pnlDetailBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 209, 231)));

        lblDetailMetode.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDetailMetode.setForeground(new java.awt.Color(48, 35, 109));
        lblDetailMetode.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetailMetode.setText("Detail Pembayaran");

        lblPetunjukBayar.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblPetunjukBayar.setForeground(new java.awt.Color(110, 110, 110));
        lblPetunjukBayar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPetunjukBayar.setText("Informasi Pembayaran");

        javax.swing.GroupLayout pnlDetailBayarLayout = new javax.swing.GroupLayout(pnlDetailBayar);
        pnlDetailBayar.setLayout(pnlDetailBayarLayout);
        pnlDetailBayarLayout.setHorizontalGroup(
            pnlDetailBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailBayarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetailBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDetailMetode, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPetunjukBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDetailBayarLayout.setVerticalGroup(
            pnlDetailBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailBayarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDetailMetode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPetunjukBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pnlTotalBayar.setBackground(new java.awt.Color(255, 255, 255));
        pnlTotalBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 209, 231)));

        lblTotalBayar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalBayar.setForeground(new java.awt.Color(48, 35, 109));
        lblTotalBayar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalBayar.setText("Total Pembayaran");

        lblBanyakTiket.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblBanyakTiket.setForeground(new java.awt.Color(110, 110, 110));
        lblBanyakTiket.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBanyakTiket.setText("Total Tiket");

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(110, 110, 110));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotal.setText("Total Bayar");

        javax.swing.GroupLayout pnlTotalBayarLayout = new javax.swing.GroupLayout(pnlTotalBayar);
        pnlTotalBayar.setLayout(pnlTotalBayarLayout);
        pnlTotalBayarLayout.setHorizontalGroup(
            pnlTotalBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalBayarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTotalBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBanyakTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(281, Short.MAX_VALUE))
        );
        pnlTotalBayarLayout.setVerticalGroup(
            pnlTotalBayarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalBayarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTotalBayar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBanyakTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnBayar.setBackground(new java.awt.Color(118, 87, 189));
        btnBayar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBayar.setForeground(new java.awt.Color(255, 255, 255));
        btnBayar.setText("Pesan Tiket");
        btnBayar.setFocusable(false);
        btnBayar.addActionListener(this::btnBayarActionPerformed);

        btnKembali.setBackground(new java.awt.Color(216, 209, 231));
        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(48, 35, 109));
        btnKembali.setText("Kembali");
        btnKembali.setFocusable(false);
        btnKembali.addActionListener(this::btnKembaliActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlRingkasan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlMetodeBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDetailBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRingkasan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlMetodeBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(pnlDetailBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(pnlTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rbQRISActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbQRISActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbQRISActionPerformed

    private void rbKasirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbKasirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbKasirActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        // TODO add your handling code here:
        simpanPemesanan();
    }//GEN-LAST:event_btnBayarActionPerformed

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        java.awt.Window window
                = javax.swing.SwingUtilities.getWindowAncestor(this);

        if (window instanceof UserMainFrame frame) {
            frame.tampilkanPanel(
                    new SeatSelectionPanel(jadwal, idUser)
            );
        }
    }//GEN-LAST:event_btnKembaliActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnKembali;
    private javax.swing.JLabel lblBanyakTiket;
    private javax.swing.JLabel lblDescKasir;
    private javax.swing.JLabel lblDescQRIS;
    private javax.swing.JLabel lblDescTransfer;
    private javax.swing.JLabel lblDetailMetode;
    private javax.swing.JLabel lblGenre;
    private javax.swing.JLabel lblHargaPerTiket;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblHeader1;
    private javax.swing.JLabel lblJamPembayaran;
    private javax.swing.JLabel lblJudulFilm;
    private javax.swing.JLabel lblJumlahTiket;
    private javax.swing.JLabel lblKursiPembayaran;
    private javax.swing.JLabel lblMetodeBayar;
    private javax.swing.JLabel lblPetunjukBayar;
    private javax.swing.JLabel lblRingkasan;
    private javax.swing.JLabel lblStudioPembayaran;
    private javax.swing.JLabel lblSubMetodeBayar;
    private javax.swing.JLabel lblTanggalPembayaran;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalBayar;
    private javax.swing.JLabel lblTotalPembayaran;
    private javax.swing.JPanel pnlDetailBayar;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMetodeBayar;
    private javax.swing.JPanel pnlRingkasan;
    private javax.swing.JPanel pnlTotalBayar;
    private javax.swing.JRadioButton rbKasir;
    private javax.swing.JRadioButton rbQRIS;
    private javax.swing.JRadioButton rbTransfer;
    // End of variables declaration//GEN-END:variables
}
