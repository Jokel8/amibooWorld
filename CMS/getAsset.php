<?php
header("Content-Type: application/json");

require_once 'dbConnect.php';
$pdo = getDBConnection();

//? Example Use getAsset.php?action=getAmount&type=character



//! Fehlerfälle
if (!isset($_GET['action'])) {
    echo json_encode(["Parameter Error" => "Bitte geben Sie 'action' an.", "Available options" => "getAmount, getImg, getTitle, getInfo", "Advice" => "getAsset.php?action=getAmount&type=character"]);
    exit(400);
}

if(!isset($_GET['type'])){
    echo json_encode(["Parameter Error" => "Bitte geben Sie 'type' an.", "Available options" => "character, item, field_type", "Advice" => "getAsset.php?action=getAmount&type=character"]);
    exit(400);
}

if (($_GET['action'] == 'getImg' || $_GET['action'] == 'getInfo') && (!isset($_GET['id']) || !is_numeric($_GET['id']))) {
    echo json_encode(["Parameter Error" => "Bitte geben Sie 'id' als Zahl an.", "Advice" => "getAsset.php?action=getImg&type=character&id=1"]);
    exit(400);
}

$table = $_GET['type'];




//! Aktionen
if ($_GET['action'] == 'getAmount') {
    $stmt = $pdo->query("SELECT COUNT(*) AS total FROM `$table`");
    $row = $stmt->fetch();
    echo json_encode(["total" => $row['total']]);
    exit(1);
}

if ($_GET['action'] == 'getImg') {
    $id = $_GET['id'];

    $stmt = $pdo->prepare("SELECT {$table}_img FROM `$table` WHERE {$table}_id = ?");
    $stmt->execute([$id]);
    $element = $stmt->fetch();

    if ($element) {
        header("Content-Type: image/jpeg");
        echo $element["{$table}_img"];
    } else {
        echo json_encode(["ID Error" => "Das angeforderte Element ist unter dieser id nicht verfügbar"]);
        http_response_code(204);
    }
}

if ($_GET['action'] == 'getTitle') {
    $id = $_GET['id'];

    $stmt = $pdo->prepare("SELECT {$table}_name FROM `$table` WHERE {$table}_id = ?");
    $stmt->execute([$id]);
    $element = $stmt->fetch();

    if ($element) {
        echo json_encode(['title' => $element["{$table}_name"]]);
    } else {
        echo json_encode(["ID Error" => "Das angeforderte Element ist unter dieser id nicht verfügbar"]);
        http_response_code(204);
    }
}

if ($_GET['action'] == 'getInfo') {
    $id = $_GET['id'];

    $stmt = $pdo->prepare("SELECT {$table}_name, {$table}_img FROM `$table` WHERE {$table}_id = ?");
    $stmt->execute([$id]);
    $character = $stmt->fetch();

    if ($character) {
        header("Content-Type: image/jpeg");
        echo $character["{$table}_img"];
    } else {
        echo json_encode(["ID Error" => "Das angeforderte Element ist unter dieser id nicht verfügbar"]);
        http_response_code(204);
    }
}
