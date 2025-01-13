<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tile Art Upload</title>
</head>
<body>
    <form action="upload.php" method="POST" enctype="multipart/form-data">
        <label for="item_name">Item benennen:</label>
        <input type="text" name="item_name" id="name" required>
        <br><br>
        <label for="image">Bild hochladen:</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <label for="item_value">Wert (in Euro):</label>
        <input type="number" name="item_value" value="0" required>
        <br><br>
        <label for="item_damage_mult">Schadenmultiplizierer (0 bis 100):</label>
        <input type="number" name="item_damage_mult" value="0" required>
        <br><br>
        <label for="item_health_eat">Gesundheitswert (in HP):</label>
        <input type="number" name="item_health_eat" value="0" required>
        <br><br>
        <label for="item_weight">Gewicht (in Gramm):</label>
        <input type="number" name="item_weight" value="0" required>
        <br><br>
        <var><label for="item_durability">Halbarkeit (in Nutzungen):</label>
        <input type="number" name="item_durability" value="0" required>
        <br><br>
        <label for="image">Fun Factor (1 bis 100):</label>
        <input type="item_fun_factor" name="item_fun_factor" value="0" required>
        <br><br></var>
        <button type="submit">Hochladen</button>
    </form>
</body>
</html>
