<?php
require_once 'dbConnect.php';
$pdo = getDBConnection();

if (isset($_GET['id']) && is_numeric($_GET['id'])) {
    $id = $_GET['id'];

    // SQL-Abfrage für den Charakter
    $stmt = $pdo->prepare("SELECT character_name, character_img FROM characters WHERE character_id = ?");
    $stmt->execute([$id]);
    $character = $stmt->fetch();

    if ($character) {
        if (isset($_GET['action']) && $_GET['action'] === 'title') {
            header("Content-Type: application/json");
            echo json_encode(['title' => $character['character_name']]);
        } else {
            header("Content-Type: image/jpeg");
            echo $character['character_img'];
        }
    } else {
        http_response_code(404);
        echo "Charakter nicht gefunden.";
    }
} else {
    http_response_code(400);
    echo "Ungültige ID.";
}
