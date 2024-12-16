<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $conn = new mysqli("v073086.kasserver.com", "d0421573", "pZuw7TVdwLCqWUjMUD8o", "d0421573");

    if ($conn->connect_error) {
        die("Verbindung fehlgeschlagen: " . $conn->connect_error);
    }

    $type_id = intval($_POST['type_id']);
    $image = $_FILES['image'];

    if ($image['error'] === UPLOAD_ERR_OK) {
        $tmp_name = $image['tmp_name'];
        $image_blob = file_get_contents($tmp_name);
        $image_type = $image['type'];

        echo $image_type, $type_id;
        $stmt = $conn->prepare("UPDATE field_type SET type_img = ?, type_img_type = ? WHERE type_id = ?");
        $stmt->bind_param("ssi", $image_blob, $image_type, $type_id);

        if ($stmt->execute()) {
            echo "Bild erfolgreich aktualisiert!";
        } else {
            echo "Fehler beim Aktualisieren: " . $stmt->error;
        }

        $stmt->close();
    } else {
        echo "Fehler: Keine Datei hochgeladen.";
    }

    $conn->close();
}
?>
