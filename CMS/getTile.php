<?php
include 'dbConnect.php';

$pdo = getDBConnection();

// SQL-Abfrage zum Lesen des BLOBs
$stmt = $pdo->prepare('SELECT type_img FROM field_type WHERE type_id = ?');
$stmt->execute([$_GET['id']]);

// Daten auslesen
$row = $stmt->fetch(PDO::FETCH_ASSOC);

if ($row) {
    // Setze den Content-Type Header (z.B. für ein Bild)
    header("Content-Type: image/png"); // Oder image/png je nach Format
    echo $row['type_img']; // Direkt das BLOB ausgeben
} else {
    echo "Bild nicht gefunden.";
}
?>