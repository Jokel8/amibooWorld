<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $conn = new mysqli("v073086.kasserver.com", "d0421573", "pZuw7TVdwLCqWUjMUD8o", "d0421573");

    if ($conn->connect_error) {
        die("Verbindung fehlgeschlagen: " . $conn->connect_error);
    }

    $image = $_FILES['image'];

    if ($image['error'] === UPLOAD_ERR_OK) {
        $tmp_name = $image['tmp_name'];
        $image_blob = file_get_contents($tmp_name);
        $image_type = $image['type'];

        echo $image_type;

        if (isset($_POST['item_name'])) {
            $stmt = $conn->prepare("INSERT INTO items (item_name, item_img, item_img_type, item_value, item_damage_mult, item_health_eat, item_weight, item_durability, item_fun_factor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("sssiiiiii", $_POST['item_name'], $image_blob, $image_type, $_POST['item_value'], $_POST['item_damage_mult'], $_POST['item_health_eat'], $_POST['item_weight'], $_POST['item_durability'], $_POST['item_fun_factor']);
        
        } elseif (isset($_POST['character_name'])) {
            $stmt = $conn->prepare("INSERT INTO characters (character_name, character_img, character_img_type) VALUES (?, ?, ?)");
            $stmt->bind_param("ssi", $_POST['character_name'], $image_blob, $image_type);

        } elseif (isset($_POST['field_id'])) {
            $stmt = $conn->prepare("UPDATE field_type SET type_img = ?, type_img_type = ? WHERE type_id = ?");
            $stmt->bind_param("ssi", $image_blob, $image_type, intval($_POST['type_id']));

        } else {
            echo "Es konnte nicht erkannt werden welche Art Asset Sie hochladen wollen :/";
        }

        $result = $stmt->execute();

        if ($result) {
            echo "Erfolgreich aktualisiert!";
        } else {
            echo "Fehler beim Aktualisieren: " . $stmt->error;
        }

        $stmt->close();
    } else {
        echo "Fehler: Keine Datei hochgeladen.";
    }

    $conn->close();
}
