<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tile Art Upload</title>
</head>
<body>
    <form action="upload.php" method="POST" enctype="multipart/form-data">
        <label for="type">Tile Art auswählen:</label>
        <select name="type_id" id="type" required>
            <?php
            // Verbindung zur Datenbank herstellen
            $conn = new mysqli("v073086.kasserver.com", "d0421573", "pZuw7TVdwLCqWUjMUD8o", "d0421573");

            // Überprüfen, ob die Verbindung funktioniert hat
            if ($conn->connect_error) {
                die("Verbindung fehlgeschlagen: " . $conn->connect_error);
            }

            // Feldtypen abrufen
            $sql = "SELECT type_id, type_name FROM field_type";
            $result = $conn->query($sql);

            if ($result->num_rows > 0) {
                // Optionen erstellen
                while ($row = $result->fetch_assoc()) {
                    echo "<option value='" . $row['type_id'] . "'>" . htmlspecialchars($row['type_name']) . "</option>";
                }
            } else {
                echo "<option value=''>Keine Optionen verfügbar</option>";
            }

            $conn->close();
            ?>
        </select>
        <br><br>
        <label for="image">Bild hochladen:</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <button type="submit">Hochladen</button>
    </form>
</body>
</html>
