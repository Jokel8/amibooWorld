<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tile Art Upload</title>
</head>
<body>
    <form action="upload.php" method="POST" enctype="multipart/form-data">
        <label for="character_name">Character benennen:</label>
        <input type="text" name="character_name" id="name" required>
        <br><br>
        <label for="image">Bild hochladen:</label>
        <input type="file" name="image" id="image" accept="image/*" required>
        <br><br>
        <button type="submit">Hochladen</button>
    </form>
</body>
</html>
