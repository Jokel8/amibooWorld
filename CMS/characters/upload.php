<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $conn = new mysqli("v073086.kasserver.com", "d0421573", "pZuw7TVdwLCqWUjMUD8o", "d0421573");

    if ($conn->connect_error) {
        die("Verbindung fehlgeschlagen: " . $conn->connect_error);
    }

    $name = $_POST['character_name'];
    $image = $_FILES['image'];

    if ($image['error'] === UPLOAD_ERR_OK) {
        $tmp_name = $image['tmp_name'];
        $image_blob = file_get_contents($tmp_name);
        $image_type = $image['type'];

        echo $image_type;
        $stmt = $conn->prepare("INSERT INTO characters (character_name, character_img, img_type) VALUES (?, ?, ?)");
        $stmt->bind_param("ssi", $name, $image_blob, $image_type);
        $result = $stmt->execute();

        if ($result) {
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
