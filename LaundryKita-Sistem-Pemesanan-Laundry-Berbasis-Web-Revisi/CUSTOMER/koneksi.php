// koneksi.php - koneksi PHP ke MySQL menggunakan mysqli
<?php
$host = 'localhost';
$user = 'root';
$pass = '';
$db   = 'laundrykita';
 
// Membuka koneksi ke server MySQL
$koneksi = mysqli_connect($host, $user, $pass, $db);
 
// Periksa apakah koneksi berhasil
if (!$koneksi) {
die('Koneksi database gagal: ' . mysqli_connect_error());
}
?>
