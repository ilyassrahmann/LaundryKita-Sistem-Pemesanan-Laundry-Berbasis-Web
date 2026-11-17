

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";




--
-- Database: `laundrykita`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id_admin` int(10) UNSIGNED NOT NULL,
  `name_admin` varchar(100) NOT NULL,
  `email_admin` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_admin`, `name_admin`, `email_admin`, `password_hash`) VALUES
(1, 'Admin LaundryKita', 'admin@laundrykita.com', '$2y$10$W1xKczekqlDmduZikugOHO4Cx4GtvDvy2sDD3RQcCPRRw5O3Kwyby'),
(2, 'Petugas Kasir', 'kasir@laundrykita.com', '$2y$10$W1xKczekqlDmduZikugOHO4Cx4GtvDvy2sDD3RQcCPRRw5O3Kwyby');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `id_customer` int(10) UNSIGNED NOT NULL,
  `name_customer` varchar(100) NOT NULL,
  `email_customer` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`id_customer`, `name_customer`, `email_customer`, `password_hash`, `phone`, `address`) VALUES
(1, 'Salwa', 'salwa@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '081234567890', 'Jl. Contoh No. 10, Pekanbaru'),
(2, 'ilyas', 'ilyas@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '082345678901', 'Jl. Merdeka No. 5, Pekanbaru'),
(3, 'Rina', 'rina@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '083456789012', 'Jl. Sudirman No. 12, Pekanbaru'),
(4, 'Dinda', 'dinda@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '084567890123', 'Jl. Ahmad Yani No. 8, Pekanbaru'),
(5, 'islam', 'islam@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '085211223344', 'Jl. Soekarno-Hatta No. 77, Pekanbaru'),
(6, 'dihar', 'dihar@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '081365778899', 'Jl. Arifin Achmad No. 21, Pekanbaru'),
(7, 'fakhry', 'fakhry@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '082171990011', 'Jl. Tuanku Tambusai No. 45, Pekanbaru'),
(8, 'faris', 'faris@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '085766554433', 'Jl. HR Soebrantas Km 12, Pekanbaru'),
(9, 'farel', 'farel@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '081276543210', 'Jl. Sisingamangaraja No. 3, Pekanbaru'),
(10, 'averoes', 'averoes@email.com', '$2y$10$w/QGotMDD/ifE8XRnYpsh.fff.HQCCGtbwS4SauG2BkynJkSOe1na', '083178901234', 'Jl. Riau No. 150, Pekanbaru'),
(13, 'reno', 'reno@email.com', '$2y$10$.uvk6wLP3BaemhsI3Aok.OoYkPuWrwugyJIYti0sO7kxd5FxQCnrO', '08126317432', NULL);

-- -------------------------------------------------------- 

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id_order` int(10) UNSIGNED NOT NULL,
  `id_customer` int(10) UNSIGNED NOT NULL,
  `id_admin` int(10) UNSIGNED DEFAULT NULL,
  `order_date` date NOT NULL,
  `weight` decimal(6,2) NOT NULL DEFAULT 0.00,
  `total_price` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `status` enum('Menunggu','Diproses','Dicuci','Disetrika','Selesai') NOT NULL DEFAULT 'Menunggu',
  `destination` varchar(255) NOT NULL,
  `method_payment` enum('Tunai','Transfer','QRIS') NOT NULL DEFAULT 'Tunai',
  `status_payment` enum('Belum Bayar','Lunas') NOT NULL DEFAULT 'Belum Bayar',
  `notes` varchar(255) DEFAULT NULL,
  `estimated_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id_order`, `id_customer`, `id_admin`, `order_date`, `weight`, `total_price`, `status`, `destination`, `method_payment`, `status_payment`, `notes`, `estimated_date`) VALUES
(1, 5, 1, '2026-09-14', 1.50, 15000, 'Selesai', 'Jl. Soekarno-Hatta No. 77, Pekanbaru', 'Transfer', 'Lunas', 'Ada noda di kemeja', '2026-09-15'),
(2, 2, 2, '2026-09-16', 5.00, 36500, 'Selesai', 'Jl. Merdeka No. 5, Pekanbaru', 'Tunai', 'Lunas', NULL, '2026-09-17'),
(3, 6, 1, '2026-09-19', 6.00, 49500, 'Selesai', 'Jl. Arifin Achmad No. 21, Pekanbaru', 'Transfer', 'Lunas', NULL, '2026-09-21'),
(4, 1, 2, '2026-09-21', 5.00, 39500, 'Selesai', 'Jl. Contoh No. 10, Pekanbaru', 'Transfer', 'Lunas', 'Ada noda di kemeja', '2026-09-24'),
(5, 3, 1, '2026-09-24', 4.00, 31000, 'Selesai', 'Jl. Sudirman No. 12, Pekanbaru', 'Transfer', 'Lunas', 'Pakaian putih dipisah', '2026-09-27'),
(6, 7, 2, '2026-09-26', 9.00, 78000, 'Selesai', 'Jl. Tuanku Tambusai No. 45, Pekanbaru', 'QRIS', 'Lunas', 'Jangan pakai pewangi', '2026-09-27'),
(7, 4, 1, '2026-09-29', 10.00, 84000, 'Selesai', 'Jl. Ahmad Yani No. 8, Pekanbaru', 'Tunai', 'Lunas', NULL, '2026-10-01'),
(8, 8, 2, '2026-10-01', 1.50, 9000, 'Selesai', 'Jl. HR Soebrantas Km 12, Pekanbaru', 'Transfer', 'Lunas', 'Jangan pakai pewangi', '2026-10-04'),
(9, 1, 1, '2026-10-03', 2.50, 25000, 'Selesai', 'Jl. Contoh No. 10, Pekanbaru', 'QRIS', 'Lunas', 'Tolong cepat, dipakai akhir pekan', '2026-10-04'),
(10, 9, 2, '2026-10-05', 7.00, 58000, 'Selesai', 'Jl. Sisingamangaraja No. 3, Pekanbaru', 'QRIS', 'Lunas', 'Ada noda di kemeja', '2026-10-08'),
(11, 2, 1, '2026-10-07', 5.50, 43000, 'Selesai', 'Jl. Merdeka No. 5, Pekanbaru', 'Tunai', 'Lunas', NULL, '2026-10-09'),
(12, 10, 2, '2026-10-09', 5.00, 30000, 'Selesai', 'Jl. Riau No. 150, Pekanbaru', 'Transfer', 'Lunas', 'Tolong cepat, dipakai akhir pekan', '2026-10-12'),
(13, 5, 1, '2026-10-12', 5.00, 50000, 'Selesai', 'Jl. Soekarno-Hatta No. 77, Pekanbaru', 'Transfer', 'Lunas', 'Ada noda di kemeja', '2026-10-14'),
(14, 1, 2, '2026-10-14', 6.50, 42000, 'Selesai', 'Jl. Contoh No. 10, Pekanbaru', 'Transfer', 'Lunas', NULL, '2026-10-16'),
(15, 3, 1, '2026-10-16', 10.50, 87000, 'Selesai', 'Jl. Sudirman No. 12, Pekanbaru', 'Transfer', 'Lunas', NULL, '2026-10-19'),
(16, 6, 2, '2026-10-18', 6.00, 40000, 'Selesai', 'Jl. Arifin Achmad No. 21, Pekanbaru', 'Tunai', 'Belum Bayar', NULL, '2026-10-20'),
(17, 7, 1, '2026-10-20', 1.50, 15000, 'Selesai', 'Jl. Tuanku Tambusai No. 45, Pekanbaru', 'Tunai', 'Lunas', NULL, '2026-10-23'),
(18, 4, 2, '2026-10-22', 1.00, 6000, 'Selesai', 'Jl. Ahmad Yani No. 8, Pekanbaru', 'QRIS', 'Lunas', NULL, '2026-10-25'),
(19, 8, 1, '2026-10-24', 8.50, 54500, 'Selesai', 'Jl. HR Soebrantas Km 12, Pekanbaru', 'QRIS', 'Lunas', NULL, '2026-10-27'),
(20, 2, 2, '2026-10-26', 2.00, 20000, 'Disetrika', 'Jl. Merdeka No. 5, Pekanbaru', 'Transfer', 'Lunas', 'Ada noda di kemeja', '2026-10-29'),
(21, 9, 1, '2026-10-27', 2.00, 20000, 'Dicuci', 'Jl. Sisingamangaraja No. 3, Pekanbaru', 'QRIS', 'Lunas', NULL, '2026-10-28'),
(22, 1, 2, '2026-10-28', 5.00, 44000, 'Diproses', 'Jl. Contoh No. 10, Pekanbaru', 'Tunai', 'Belum Bayar', 'Ada noda di kemeja', '2026-10-29'),
(23, 10, 1, '2026-10-28', 5.50, 37000, 'Diproses', 'Jl. Riau No. 150, Pekanbaru', 'Tunai', 'Lunas', 'Pakaian putih dipisah', '2026-10-30'),
(25, 4, 1, '2026-10-29', 5.00, 40000, 'Selesai', 'Jl. Ahmad Yani No. 8, Pekanbaru', 'Transfer', 'Belum Bayar', 'Tolong cepat, dipakai akhir pekan', '2026-10-30'),
(26, 6, 2, '2026-10-30', 10.00, 78500, 'Menunggu', 'Jl. Arifin Achmad No. 21, Pekanbaru', 'QRIS', 'Belum Bayar', NULL, '2026-10-31'),
(27, 5, 1, '2026-10-30', 3.00, 20000, 'Menunggu', 'Jl. Soekarno-Hatta No. 77, Pekanbaru', 'QRIS', 'Belum Bayar', 'Tolong cepat, dipakai akhir pekan', '2026-10-31'),
(28, 2, 1, '2026-10-30', 1.00, 7000, 'Menunggu', 'Jl. Merdeka No. 5, Pekanbaru', 'Tunai', 'Belum Bayar', NULL, '2026-10-31'),
(29, 6, 1, '2026-10-30', 2.00, 14000, 'Menunggu', 'Jl. Arifin Achmad No. 21, Pekanbaru', 'Tunai', 'Belum Bayar', 'dia bon tenggat waktu seminggu', '2026-11-01');

-- --------------------------------------------------------

--
-- Table structure for table `order_detail`
--

CREATE TABLE `order_detail` (
  `id_order_detail` int(10) UNSIGNED NOT NULL,
  `id_order` int(10) UNSIGNED NOT NULL,
  `id_service` int(10) UNSIGNED NOT NULL,
  `weight_kg` decimal(6,2) NOT NULL,
  `price_per_kg` int(10) UNSIGNED NOT NULL,
  `subtotal` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_detail`
--

INSERT INTO `order_detail` (`id_order_detail`, `id_order`, `id_service`, `weight_kg`, `price_per_kg`, `subtotal`) VALUES
(1, 1, 2, 1.50, 10000, 15000),
(2, 2, 1, 2.50, 7000, 17500),
(3, 2, 2, 1.00, 10000, 10000),
(4, 2, 3, 1.50, 6000, 9000),
(5, 3, 1, 3.50, 7000, 24500),
(6, 3, 2, 2.50, 10000, 25000),
(7, 4, 1, 3.50, 7000, 24500),
(8, 4, 2, 1.50, 10000, 15000),
(9, 5, 1, 3.00, 7000, 21000),
(10, 5, 2, 1.00, 10000, 10000),
(11, 6, 1, 4.00, 7000, 28000),
(12, 6, 2, 5.00, 10000, 50000),
(13, 7, 1, 4.00, 7000, 28000),
(14, 7, 2, 5.00, 10000, 50000),
(15, 7, 3, 1.00, 6000, 6000),
(16, 8, 3, 1.50, 6000, 9000),
(17, 9, 2, 2.50, 10000, 25000),
(18, 10, 2, 4.00, 10000, 40000),
(19, 10, 3, 3.00, 6000, 18000),
(20, 11, 2, 2.50, 10000, 25000),
(21, 11, 3, 3.00, 6000, 18000),
(22, 12, 3, 5.00, 6000, 30000),
(23, 13, 2, 5.00, 10000, 50000),
(24, 14, 1, 3.00, 7000, 21000),
(25, 14, 3, 3.50, 6000, 21000),
(26, 15, 1, 4.00, 7000, 28000),
(27, 15, 2, 5.00, 10000, 50000),
(28, 15, 3, 1.50, 6000, 9000),
(29, 16, 1, 4.00, 7000, 28000),
(30, 16, 3, 2.00, 6000, 12000),
(31, 17, 2, 1.50, 10000, 15000),
(32, 18, 3, 1.00, 6000, 6000),
(33, 19, 1, 3.50, 7000, 24500),
(34, 19, 3, 5.00, 6000, 30000),
(35, 20, 2, 2.00, 10000, 20000),
(36, 21, 2, 2.00, 10000, 20000),
(37, 22, 2, 3.00, 10000, 30000),
(38, 22, 1, 2.00, 7000, 14000),
(39, 23, 1, 4.00, 7000, 28000),
(40, 23, 3, 1.50, 6000, 9000),
(42, 25, 1, 2.00, 7000, 14000),
(43, 25, 2, 2.00, 10000, 20000),
(44, 25, 3, 1.00, 6000, 6000),
(45, 26, 1, 2.50, 7000, 17500),
(46, 26, 2, 4.00, 10000, 40000),
(47, 26, 3, 3.50, 6000, 21000),
(48, 27, 1, 2.00, 7000, 14000),
(49, 27, 3, 1.00, 6000, 6000),
(50, 28, 1, 1.00, 7000, 7000),
(51, 29, 1, 2.00, 7000, 14000);

-- --------------------------------------------------------

--
-- Table structure for table `service`
--

CREATE TABLE `service` (
  `id_service` int(10) UNSIGNED NOT NULL,
  `service_type` varchar(100) NOT NULL,
  `price_per_kg` int(10) UNSIGNED NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service`
--

INSERT INTO `service` (`id_service`, `service_type`, `price_per_kg`, `category`, `description`) VALUES
(1, 'Cuci Kering', 7000, 'Kiloan', 'Cucian dicuci bersih lalu dikeringkan, siap dipakai.'),
(2, 'Cuci Setrika', 10000, 'Kiloan', 'Dicuci, dikeringkan, dan disetrika rapi.'),
(3, 'Setrika Saja', 6000, 'Kiloan', 'Khusus pakaian bersih yang hanya perlu disetrika.');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_admin`),
  ADD UNIQUE KEY `uq_admin_email` (`email_admin`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`id_customer`),
  ADD UNIQUE KEY `uq_customer_email` (`email_customer`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id_order`),
  ADD KEY `idx_orders_customer` (`id_customer`),
  ADD KEY `idx_orders_admin` (`id_admin`);

--
-- Indexes for table `order_detail`
--
ALTER TABLE `order_detail`
  ADD PRIMARY KEY (`id_order_detail`),
  ADD KEY `idx_detail_order` (`id_order`),
  ADD KEY `idx_detail_service` (`id_service`);

--
-- Indexes for table `service`
--
ALTER TABLE `service`
  ADD PRIMARY KEY (`id_service`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id_admin` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `id_customer` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id_order` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `order_detail`
--
ALTER TABLE `order_detail`
  MODIFY `id_order_detail` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- AUTO_INCREMENT for table `service`
--
ALTER TABLE `service`
  MODIFY `id_service` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_admin` FOREIGN KEY (`id_admin`) REFERENCES `admin` (`id_admin`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_orders_customer` FOREIGN KEY (`id_customer`) REFERENCES `customer` (`id_customer`) ON UPDATE CASCADE;

--
-- Constraints for table `order_detail`
--
ALTER TABLE `order_detail`
  ADD CONSTRAINT `fk_detail_order` FOREIGN KEY (`id_order`) REFERENCES `orders` (`id_order`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detail_service` FOREIGN KEY (`id_service`) REFERENCES `service` (`id_service`) ON UPDATE CASCADE;
COMMIT;


