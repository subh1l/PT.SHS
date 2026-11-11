CREATE TABLE `users` (
  `id_user` int PRIMARY KEY AUTO_INCREMENT,
  `nama` varchar(100),
  `email` varchar(100) UNIQUE,
  `password` varchar(255),
  `role` enum('user','admin'),
  `created_at` timestamp
);

CREATE TABLE `films` (
  `id_film` int PRIMARY KEY AUTO_INCREMENT,
  `judul` varchar(150),
  `genre` varchar(50),
  `durasi_menit` int,
  `rating_usia` varchar(10),
  `sinopsis` text,
  `status` enum('Tayang','Segera')
);

CREATE TABLE `studios` (
  `id_studio` int PRIMARY KEY AUTO_INCREMENT,
  `nama_studio` varchar(50),
  `tipe` enum('IMAX','Reguler'),
  `kapasitas` int
);

CREATE TABLE `jadwal` (
  `id_jadwal` int PRIMARY KEY AUTO_INCREMENT,
  `id_film` int NOT NULL,
  `id_studio` int NOT NULL,
  `tanggal` date,
  `jam_mulai` time,
  `harga` decimal(10,2)
);

CREATE TABLE `kursi` (
  `id_kursi` int PRIMARY KEY AUTO_INCREMENT,
  `id_studio` int NOT NULL,
  `baris` char(1),
  `nomor` int
);

CREATE TABLE `pemesanan` (
  `id_pemesanan` int PRIMARY KEY AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `id_jadwal` int NOT NULL,
  `tanggal_pesan` datetime,
  `total` decimal(10,2),
  `status` enum('Sukses','Gagal','Pending')
);

CREATE TABLE `pembayaran` (
  `id_pembayaran` int PRIMARY KEY AUTO_INCREMENT,
  `id_pemesanan` int NOT NULL,
  `metode` enum('Dana','GoPay', 'Bank'),
  `jumlah` decimal(10,2),
  `tanggal_bayar` datetime,
  `status` enum('Sukses','Gagal')
);

CREATE TABLE `detail_pemesanan` (
  `id_detail` int PRIMARY KEY AUTO_INCREMENT,
  `id_pemesanan` int NOT NULL,
  `id_kursi` int NOT NULL,
  `harga` decimal(10,2)
);

ALTER TABLE `jadwal` ADD FOREIGN KEY (`id_film`) REFERENCES `films` (`id_film`);

ALTER TABLE `jadwal` ADD FOREIGN KEY (`id_studio`) REFERENCES `studios` (`id_studio`);

ALTER TABLE `kursi` ADD FOREIGN KEY (`id_studio`) REFERENCES `studios` (`id_studio`);

ALTER TABLE `pemesanan` ADD FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`);

ALTER TABLE `pemesanan` ADD FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal` (`id_jadwal`);

ALTER TABLE `pembayaran` ADD FOREIGN KEY (`id_pemesanan`) REFERENCES `pemesanan` (`id_pemesanan`);

ALTER TABLE `detail_pemesanan` ADD FOREIGN KEY (`id_pemesanan`) REFERENCES `pemesanan` (`id_pemesanan`);

ALTER TABLE `detail_pemesanan` ADD FOREIGN KEY (`id_kursi`) REFERENCES `kursi` (`id_kursi`);
