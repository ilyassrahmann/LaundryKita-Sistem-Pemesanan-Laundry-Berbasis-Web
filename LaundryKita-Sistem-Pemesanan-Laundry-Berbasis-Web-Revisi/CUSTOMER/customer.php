<?php
// customer.php - halaman customer, daftar layanan diambil dari database
require_once 'koneksi.php';

// Ambil semua layanan, urut berdasarkan id
$sql    = 'SELECT * FROM layanan ORDER BY id_layanan ASC';
$result = mysqli_query($koneksi, $sql);

// Gambar tiap layanan (cocokkan dengan nama_layanan, huruf kecil)
$gambar = [
    'cuci kering'  => 'kering.png',
    'cuci setrika' => 'setrika.png',
    'setrika saja' => 'setrika saja.png',
];
?>
<!DOCTYPE html>
<html lang="id">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>LaundryKita - Customer</title>

    <!-- Bootstrap -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet">

    <!-- CSS -->
    <link rel="stylesheet" href="../CSS/style.css">
</head>

<body>

    <!-- NAVBAR -->
    <nav class="navbar navbar-expand-lg bg-white shadow-sm">

        <div class="container">

            <!-- LOGO -->
            <a class="navbar-brand fw-bold" href="customer.php">
                LaundryKita
            </a>


            <!-- TOGGLE MOBILE -->
            <button
                class="navbar-toggler"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target="#navbarMenu"
                aria-controls="navbarMenu"
                aria-expanded="false"
                aria-label="Buka menu navigasi">

                <span class="navbar-toggler-icon"></span>

            </button>


            <!-- MENU -->
            <div class="collapse navbar-collapse" id="navbarMenu">

                <ul class="navbar-nav ms-auto">

                    <!-- BERANDA -->
                    <li class="nav-item">

                        <a
                            class="nav-link active"
                            href="customer.php"
                            aria-current="page">

                            Beranda

                        </a>

                    </li>


                    <!-- PESANAN SAYA -->
                    <li class="nav-item">

                        <a
                            class="nav-link"
                            href="status-pesanan.html">

                            Pesanan Saya

                        </a>

                    </li>


                    <!-- PROFIL -->
                    <li class="nav-item">

                        <a
                            class="nav-link"
                            href="profil.html">

                            Profil

                        </a>

                    </li>


                    <!-- KELUAR -->
                    <li class="nav-item ms-lg-2">

                        <a
                            class="btn btn-outline-danger btn-sm mt-1"
                            href="../index.html">

                            Keluar

                        </a>

                    </li>

                </ul>

            </div>

        </div>

    </nav>


    <!-- HERO -->
    <section class="container py-5">

        <div class="row align-items-center">

            <!-- TEXT -->
            <div class="col-md-7">

                <h1 class="fw-bold">
                    Halo, Salwa! 👋
                </h1>

                <p class="text-muted fs-5">
                    Pantau status laundry kamu di sini.
                </p>


                <a
                    href="status-pesanan.html"
                    class="btn btn-primary px-4">

                    Lacak Pesanan

                </a>

            </div>


            <div class="col-md-5 text-center">

                <div class="img-border">
                    <img src="LaundryKita.webp" alt="Laundry Hub" class="img-fluid img-glow">
                </div>

                <h5 class="fw-bold mt-3">
                    Laundry jadi lebih mudah!
                </h5>

            </div>

        </div>

    </section>


    <!-- PESANAN AKTIF -->
    <section class="container pb-5">

        <h2 class="fw-bold mb-4">
            Pesanan Aktif
        </h2>


        <div class="card shadow-sm rounded-4 mb-4">

            <div class="card-body p-4">

                <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">

                    <div>

                        <h5 class="fw-bold mb-1">
                            Pesanan #LK003
                        </h5>

                        <small class="text-muted">
                            12 Oktober 2026
                        </small>

                    </div>

                    <span class="badge bg-primary">
                        Diproses
                    </span>

                </div>


                <hr>


                <div class="row">

                    <div class="col-md-6">

                        <p class="mb-2">
                            <strong>Layanan:</strong>
                            Cuci Setrika
                        </p>

                        <p class="mb-2">
                            <strong>Berat:</strong>
                            3 Kg
                        </p>

                    </div>


                    <div class="col-md-6">

                        <p class="mb-2">
                            <strong>Total:</strong>
                            Rp30.000
                        </p>

                    </div>

                </div>


                <ol class="order-steps mt-4 mb-3">

                    <li class="done" data-step="1">
                        Menunggu
                    </li>

                    <li class="current" data-step="2" aria-current="step">
                        Diproses
                    </li>

                    <li data-step="3">
                        Dicuci
                    </li>

                    <li data-step="4">
                        Disetrika
                    </li>

                    <li data-step="5">
                        Selesai
                    </li>

                </ol>


                <p class="text-muted mb-3">
                    Estimasi selesai: 14 Oktober 2026
                </p>


                <a
                    href="status-pesanan.html"
                    class="btn btn-primary">

                    Lihat Detail

                </a>

            </div>

        </div>


        <div id="noActiveOrder" class="card d-none shadow-sm rounded-4">

            <div class="card-body p-4 text-center text-muted">
                Belum ada pesanan aktif. Antar cucianmu ke outlet dan petugas kami akan mencatatnya.
            </div>

        </div>

    </section>


    <!-- LAYANAN -->
    <section class="container pb-5">

        <div class="text-center mb-4">

            <h2 class="fw-bold mb-1">
                Daftar Layanan &amp; Harga
            </h2>

            <p class="text-muted small mb-0">
                Harga per kilogram
            </p>

        </div>


        <div class="row g-4">

<?php if ($result && mysqli_num_rows($result) > 0) : ?>
<?php while ($row = mysqli_fetch_assoc($result)) : ?>
<?php
    $kunci = strtolower($row['nama_layanan']);
    $img   = $gambar[$kunci] ?? null;
?>

            <div class="col-md-4">

                <div class="card h-100 shadow-sm rounded-4 overflow-hidden">

                    <?php if ($img) : ?>
                    <img class="card-img-top svc-img" src="<?= rawurlencode($img) ?>" alt="<?= htmlspecialchars($row['nama_layanan']) ?>">
                    <?php endif; ?>

                    <div class="card-body">

                        <h5 class="fw-bold">
                            <?= htmlspecialchars($row['nama_layanan']) ?>
                            <span class="badge bg-light text-secondary border fw-normal fs-6">
                                <?= htmlspecialchars($row['kategori']) ?>
                            </span>
                        </h5>

                        <p class="text-muted">
                            <?= htmlspecialchars($row['deskripsi']) ?>
                        </p>

                        <p class="fw-bold fs-5 mb-0">
                            Rp<?= number_format($row['harga'], 0, ',', '.') ?> / kg
                        </p>

                    </div>

                </div>

            </div>

<?php endwhile; ?>
<?php else : ?>

            <div class="col-12 text-center text-muted">
                Belum ada layanan yang tersedia.
            </div>

<?php endif; ?>

        </div>

    </section>


    <!-- CARA MEMESAN -->
    <section class="container pb-5">

        <h2 class="fw-bold text-center mb-4">
            Cara Memesan
        </h2>


        <div class="row g-4 mb-4">

            <div class="col-md-4">

                <div class="card h-100 shadow-sm rounded-4">

                    <div class="card-body p-4 text-center">

                        <h5 class="fw-bold">
                            Antar cucianmu
                        </h5>

                        <p class="text-muted mb-0">
                            Bawa laundry ke outlet atau hubungi kami via WhatsApp.
                        </p>

                    </div>

                </div>

            </div>


            <div class="col-md-4">

                <div class="card h-100 shadow-sm rounded-4">

                    <div class="card-body p-4 text-center">

                        <h5 class="fw-bold">
                            Petugas mencatat
                        </h5>

                        <p class="text-muted mb-0">
                            Petugas menimbang dan mencatat pesananmu.
                        </p>

                    </div>

                </div>

            </div>


            <div class="col-md-4">

                <div class="card h-100 shadow-sm rounded-4">

                    <div class="card-body p-4 text-center">

                        <h5 class="fw-bold">
                            Pantau di sini
                        </h5>

                        <p class="text-muted mb-0">
                            Lihat status laundry kapan saja di halaman Pesanan Saya.
                        </p>

                    </div>

                </div>

            </div>

        </div>


        <div class="text-center">

            <a
                href="https://wa.me/6281234567890"
                class="btn btn-primary"
                target="_blank"
                rel="noopener">

                Hubungi via WhatsApp

            </a>

        </div>

    </section>


    <!-- FOOTER -->
    <footer class="text-center py-4 bg-white">

        <p class="mb-1 text-muted">

            © 2026 LaundryKita —
            Sistem Pemesanan Laundry

        </p>

        <p class="mb-0 text-muted small">
            WhatsApp 0812-3456-7890 - Buka setiap hari 08.00-20.00
        </p>

    </footer>


    <!-- Bootstrap JS -->
    <script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
    </script>

</body>

</html>

<?php mysqli_close($koneksi); ?>
