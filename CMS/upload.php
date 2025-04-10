<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $conn = new mysqli("v073086.kasserver.com", "d0421573", "pZuw7TVdwLCqWUjMUD8o", "d0421573");

    if ($conn->connect_error) {
        die("Verbindung fehlgeschlagen: " . $conn->connect_error);
    }

    $images = $_FILES['images'];

    if ($images['error'] === UPLOAD_ERR_OK || $images['error'][0] === UPLOAD_ERR_OK) {
        if (isset($_POST['item_name'])) {
            $tmp_name = $images['tmp_name'];
            $image_blob = file_get_contents($tmp_name);
            $image_type = $image['type'];

            echo $image_type;

            $stmt = $conn->prepare("INSERT INTO item (item_name, item_img, item_img_type, item_value, item_damage_mult, item_health_eat, item_weight, item_durability, item_fun_factor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("sssiiiiii", $_POST['item_name'], $image_blob, $image_type, $_POST['item_value'], $_POST['item_damage_mult'], $_POST['item_health_eat'], $_POST['item_weight'], $_POST['item_durability'], $_POST['item_fun_factor']);
        } elseif (isset($_POST['field_id'])) {
            $tmp_name = $images['tmp_name'];
            $image_blob = file_get_contents($tmp_name);
            $image_type = $image['type'];

            echo $image_type;

            $stmt = $conn->prepare("UPDATE field_type SET type_img = ?, type_img_type = ? WHERE type_id = ?");
            $stmt->bind_param("ssi", $image_blob, $image_type, intval($_POST['type_id']));
        } elseif (isset($_POST['characters'])) {
            $stmt = $conn->prepare("INSERT INTO `character` (character_name, character_img, character_img_type) VALUES (?, ?, ?)");

            for ($i = 0; $i < count($images['name']); $i++) {
                if ($images['error'][$i] === UPLOAD_ERR_OK) {
                    $tmp_name = $images['tmp_name'][$i];
                    $image_blob = file_get_contents($tmp_name);
                    $image_type = $images['type'][$i];
                    $character_name = pathinfo($images['name'][$i], PATHINFO_FILENAME);

                    $stmt->bind_param("ssi", $character_name, $image_blob, $image_type);

                    if (!$stmt->execute()) {
                        echo "Fehler beim Einfügen: " . $stmt->error . "\n";
                    } else {
                        echo "Bild \"$character_name\" erfolgreich hochgeladen.\n";
                    }
                } else {
                    echo "Fehler beim Hochladen der Datei: " . $images['name'][$i] . "\n";
                }
            }
        } elseif (isset($_POST['items'])) {
            $stmt = $conn->prepare("INSERT INTO item (item_name, item_img, item_img_type) VALUES (?, ?, ?)");

            for ($i = 0; $i < count($images['name']); $i++) {
                if ($images['error'][$i] === UPLOAD_ERR_OK) {
                    $tmp_name = $images['tmp_name'][$i];
                    $image_blob = file_get_contents($tmp_name);
                    $image_type = $images['type'][$i];
                    $character_name = pathinfo($images['name'][$i], PATHINFO_FILENAME);

                    $stmt->bind_param("ssi", $character_name, $image_blob, $image_type);

                    if (!$stmt->execute()) {
                        echo "Fehler beim Einfügen: " . $stmt->error . "\n";
                    } else {
                        echo "Bild \"$character_name\" erfolgreich hochgeladen.\n";
                    }
                } else {
                    echo "Fehler beim Hochladen der Datei: " . $images['name'][$i] . "\n";
                }
            }
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
