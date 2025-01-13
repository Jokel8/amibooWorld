<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tile Art Upload</title>
</head>
<body>
    <form action="upload.php" method="POST" enctype="multipart/form-data">
        <label for="character_name">Item benennen:</label>
        <input type="text" name="character_name" id="name" required>
        <br><br>
        <label for="image">Bild hochladen:</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <label for="image">Wert (in Euro):</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <label for="image">Schadenmultiplizierer (0 bis 100):</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <label for="image">Gesundheitswert (in HP):</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <label for="image">Gewicht (in Gramm):</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <var><label for="image">Halbarkeit (in Nutzungen):</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <label for="image">Fun Factor (1 bis 100):</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br></var>
        <button type="submit">Hochladen</button>
    </form>
</body>
</html>
