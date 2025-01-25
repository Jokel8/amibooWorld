<?php
require_once 'dbConnect.php';
$pdo = getDBConnection();

$stmt = $pdo->query("SELECT COUNT(*) AS total FROM characters");
$row = $stmt->fetch();
header("Content-Type: application/json");
echo json_encode(["total" => $row['total']]);
?>