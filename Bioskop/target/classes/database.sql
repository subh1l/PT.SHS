-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: lerd-mysql
-- Generation Time: Nov 11, 2026 at 11:43 AM
-- Server version: 8.4.13
-- PHP Version: 8.3.35

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbBioskop`
--
CREATE DATABASE IF NOT EXISTS `dbBioskop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `dbBioskop`;

-- --------------------------------------------------------

--
-- Table structure for table `detail_pemesanan`
--

CREATE TABLE `detail_pemesanan` (
  `id_detail` int NOT NULL,
  `id_pemesanan` int NOT NULL,
  `id_jadwal` int NOT NULL,
  `id_kursi` int NOT NULL,
  `harga` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `film`
--

CREATE TABLE `film` (
  `id_film` int NOT NULL,
  `judul` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `genre` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `durasi_menit` int DEFAULT NULL,
  `rating_usia` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sinopsis` text COLLATE utf8mb4_unicode_ci,
  `status` enum('Tayang','Segera') COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `jadwal`
--

CREATE TABLE `jadwal` (
  `id_jadwal` int NOT NULL,
  `id_film` int NOT NULL,
  `id_studio` int NOT NULL,
  `tanggal` date DEFAULT NULL,
  `jam_mulai` time DEFAULT NULL,
  `harga` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `kursi`
--

CREATE TABLE `kursi` (
  `id_kursi` int NOT NULL,
  `id_studio` int NOT NULL,
  `baris` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nomor` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pembayaran`
--

CREATE TABLE `pembayaran` (
  `id_pembayaran` int NOT NULL,
  `id_pemesanan` int NOT NULL,
  `metode` enum('Dana','GoPay','Bank') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jumlah` decimal(10,2) DEFAULT NULL,
  `tanggal_bayar` datetime DEFAULT NULL,
  `status` enum('Sukses','Gagal') COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pemesanan`
--

CREATE TABLE `pemesanan` (
  `id_pemesanan` int NOT NULL,
  `id_user` int NOT NULL,
  `id_jadwal` int NOT NULL,
  `tanggal_pesan` datetime DEFAULT NULL,
  `total_harga` decimal(10,2) DEFAULT NULL,
  `status` enum('Sukses','Gagal','Pending') COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `studio`
--

CREATE TABLE `studio` (
  `id_studio` int NOT NULL,
  `nama_studio` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipe` enum('IMAX','Reguler') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `kapasitas` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int NOT NULL,
  `nama` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('user','admin') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT (now())
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `detail_pemesanan`
--
ALTER TABLE `detail_pemesanan`
  ADD PRIMARY KEY (`id_detail`),
  ADD UNIQUE KEY `uk_jadwal_kursi` (`id_jadwal`,`id_kursi`),
  ADD KEY `id_pemesanan` (`id_pemesanan`),
  ADD KEY `id_kursi` (`id_kursi`);

--
-- Indexes for table `film`
--
ALTER TABLE `film`
  ADD PRIMARY KEY (`id_film`);

--
-- Indexes for table `jadwal`
--
ALTER TABLE `jadwal`
  ADD PRIMARY KEY (`id_jadwal`),
  ADD KEY `id_film` (`id_film`),
  ADD KEY `id_studio` (`id_studio`);

--
-- Indexes for table `kursi`
--
ALTER TABLE `kursi`
  ADD PRIMARY KEY (`id_kursi`),
  ADD KEY `id_studio` (`id_studio`);

--
-- Indexes for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD PRIMARY KEY (`id_pembayaran`),
  ADD KEY `id_pemesanan` (`id_pemesanan`);

--
-- Indexes for table `pemesanan`
--
ALTER TABLE `pemesanan`
  ADD PRIMARY KEY (`id_pemesanan`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `id_jadwal` (`id_jadwal`);

--
-- Indexes for table `studio`
--
ALTER TABLE `studio`
  ADD PRIMARY KEY (`id_studio`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `detail_pemesanan`
--
ALTER TABLE `detail_pemesanan`
  MODIFY `id_detail` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `film`
--
ALTER TABLE `film`
  MODIFY `id_film` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `jadwal`
--
ALTER TABLE `jadwal`
  MODIFY `id_jadwal` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `kursi`
--
ALTER TABLE `kursi`
  MODIFY `id_kursi` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pembayaran`
--
ALTER TABLE `pembayaran`
  MODIFY `id_pembayaran` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pemesanan`
--
ALTER TABLE `pemesanan`
  MODIFY `id_pemesanan` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `studio`
--
ALTER TABLE `studio`
  MODIFY `id_studio` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_pemesanan`
--
ALTER TABLE `detail_pemesanan`
  ADD CONSTRAINT `detail_pemesanan_ibfk_1` FOREIGN KEY (`id_pemesanan`) REFERENCES `pemesanan` (`id_pemesanan`),
  ADD CONSTRAINT `detail_pemesanan_ibfk_2` FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal` (`id_jadwal`),
  ADD CONSTRAINT `detail_pemesanan_ibfk_3` FOREIGN KEY (`id_kursi`) REFERENCES `kursi` (`id_kursi`);

--
-- Constraints for table `jadwal`
--
ALTER TABLE `jadwal`
  ADD CONSTRAINT `jadwal_ibfk_1` FOREIGN KEY (`id_film`) REFERENCES `film` (`id_film`),
  ADD CONSTRAINT `jadwal_ibfk_2` FOREIGN KEY (`id_studio`) REFERENCES `studio` (`id_studio`);

--
-- Constraints for table `kursi`
--
ALTER TABLE `kursi`
  ADD CONSTRAINT `kursi_ibfk_1` FOREIGN KEY (`id_studio`) REFERENCES `studio` (`id_studio`);

--
-- Constraints for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD CONSTRAINT `pembayaran_ibfk_1` FOREIGN KEY (`id_pemesanan`) REFERENCES `pemesanan` (`id_pemesanan`);

--
-- Constraints for table `pemesanan`
--
ALTER TABLE `pemesanan`
  ADD CONSTRAINT `pemesanan_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`),
  ADD CONSTRAINT `pemesanan_ibfk_2` FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal` (`id_jadwal`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
