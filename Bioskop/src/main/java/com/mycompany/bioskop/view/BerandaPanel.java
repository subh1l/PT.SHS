/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.bioskop.view;

import com.mycompany.bioskop.entity.Film;
import com.mycompany.bioskop.entity.Jadwal;
import com.mycompany.bioskop.repository.FilmRepository;
import com.mycompany.bioskop.repository.JadwalRepository;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ACER NITRO
 */
public class BerandaPanel extends javax.swing.JPanel {

    /**
     * Creates new form BerandaPanel
     */
    private final FilmRepository filmRepository = new FilmRepository();
    private final JadwalRepository jadwalRepository = new JadwalRepository();
    private List<Film> daftarFilm = new java.util.ArrayList<>();

    public BerandaPanel() {
        initComponents();
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setDefaultEditor(Object.class, null);
        jButton1.addActionListener(e -> pilihJadwal());
        btnDetail.addActionListener(e -> lihatDetail());
        muatFilm();
    }

    private void muatFilm() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        daftarFilm = new java.util.ArrayList<>();
        try {
            List<Jadwal> semuaJadwal = jadwalRepository.getAllJadwal();
            NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            for (Film f : filmRepository.getAllFilm()) {
                if (!"Tayang".equals(f.getStatus())) {
                    continue;
                }
                double termurah = 0;
                for (Jadwal j : semuaJadwal) {
                    if (j.getIdFilm() == f.getIdFilm()
                            && (termurah == 0 || j.getHarga() < termurah)) {
                        termurah = j.getHarga();
                    }
                }
                daftarFilm.add(f);
                model.addRow(new Object[]{
                    f.getJudul(),
                    f.getGenre(),
                    f.getRatingUsia(),
                    termurah == 0 ? "-" : rupiah.format(termurah)
                });
            }
            if (!daftarFilm.isEmpty()) {
                lblBanner.setText(daftarFilm.get(0).getJudul());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat film: " + e.getMessage());
        }
    }

    private Film filmTerpilih() {
        int baris = jTable1.getSelectedRow();
        if (baris == -1 || baris >= daftarFilm.size()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih film terlebih dahulu!");
            return null;
        }
        return daftarFilm.get(baris);
    }

    private void lihatDetail() {
        Film f = filmTerpilih();
        if (f == null) {
            return;
        }
        JOptionPane.showMessageDialog(this,
                f.getJudul() + "\n"
                + "Genre: " + f.getGenre() + "\n"
                + "Durasi: " + f.getDurasiMenit() + " menit\n"
                + "Rating: " + f.getRatingUsia() + "\n\n"
                + f.getSinopsis(),
                "Detail Film", JOptionPane.INFORMATION_MESSAGE);
    }

    private void pilihJadwal() {
        Film film = filmTerpilih();
        if (film == null) {
            return;
        }

        List<Jadwal> jadwalFilm = new java.util.ArrayList<>();

        for (Jadwal jadwal : jadwalRepository.getAllJadwal()) {
            if (jadwal.getIdFilm() == film.getIdFilm()) {
                jadwalFilm.add(jadwal);
            }
        }

        if (jadwalFilm.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Belum ada jadwal untuk film ini.",
                    "Jadwal tidak tersedia",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        jadwalFilm.sort(
                java.util.Comparator
                        .comparing(Jadwal::getTanggal)
                        .thenComparing(Jadwal::getJamMulai)
        );

        java.time.format.DateTimeFormatter formatTanggal
                = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy");

        java.time.format.DateTimeFormatter formatJam
                = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

        String[] pilihan = new String[jadwalFilm.size()];

        for (int i = 0; i < jadwalFilm.size(); i++) {
            Jadwal jadwal = jadwalFilm.get(i);

            pilihan[i] = jadwal.getTanggal().format(formatTanggal)
                    + " | " + jadwal.getJamMulai().format(formatJam)
                    + " | Rp" + String.format("%,.0f", jadwal.getHarga());
        }

        String jadwalDipilih = (String) JOptionPane.showInputDialog(
                this,
                "Pilih jadwal untuk " + film.getJudul() + ":",
                "Pilih Jadwal",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pilihan,
                pilihan[0]
        );

        if (jadwalDipilih == null) {
            return;
        }

        int index = java.util.Arrays.asList(pilihan).indexOf(jadwalDipilih);

        if (index < 0) {
            return;
        }

        java.awt.Window window
                = javax.swing.SwingUtilities.getWindowAncestor(this);

        if (window instanceof UserMainFrame frame) {
            frame.tampilkanPanel(
                    new SeatSelectionPanel(jadwalFilm.get(index), frame.getIdUser())
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
        lblheader = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        pnlBanner = new javax.swing.JPanel();
        lblBanner = new javax.swing.JLabel();
        btnDetail = new javax.swing.JButton();
        lblDeskripsi = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setPreferredSize(new java.awt.Dimension(600, 86));

        lblheader.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblheader.setText("Temukan film favoritmu dan nikmati pengalaman menonton bersama PT.shs Cinema");

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblWelcome.setText("Selamat Datang ");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblheader, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblWelcome)
                .addGap(18, 18, 18)
                .addComponent(lblheader)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pnlBanner.setBackground(new java.awt.Color(48, 36, 110));
        pnlBanner.setPreferredSize(new java.awt.Dimension(500, 100));

        lblBanner.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblBanner.setForeground(new java.awt.Color(255, 255, 255));
        lblBanner.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBanner.setText("Avatar: Fire and Ash");

        btnDetail.setBackground(new java.awt.Color(118, 88, 184));
        btnDetail.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDetail.setForeground(new java.awt.Color(255, 255, 255));
        btnDetail.setText("Lihat Detail");
        btnDetail.setBorderPainted(false);
        btnDetail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDetail.setFocusPainted(false);

        lblDeskripsi.setForeground(new java.awt.Color(255, 255, 255));
        lblDeskripsi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDeskripsi.setText("Film terbaru yang sedang tayang di PT.shs Cinema.");

        javax.swing.GroupLayout pnlBannerLayout = new javax.swing.GroupLayout(pnlBanner);
        pnlBanner.setLayout(pnlBannerLayout);
        pnlBannerLayout.setHorizontalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDetail)
                    .addComponent(lblDeskripsi, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBannerLayout.setVerticalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBanner)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDeskripsi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(btnDetail)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Judul Film", "Genre", "Rating", "Harga Tiket "
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Pilih Jadwal ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
                    .addComponent(pnlBanner, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDetail;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblBanner;
    private javax.swing.JLabel lblDeskripsi;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JLabel lblheader;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JPanel pnlHeader;
    // End of variables declaration//GEN-END:variables
}
