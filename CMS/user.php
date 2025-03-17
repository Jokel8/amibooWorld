<!DOCTYPE html>
<html lang="de">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management</title>
    <link rel="stylesheet" href="cms.css" />
    <?php
    require_once 'dbConnect.php';
    $pdo = getDBConnection();

    if (isset($_POST['user_id'])) {
        if (isset($_POST['delete'])) {
            $stmt = $pdo->prepare('DELETE FROM user WHERE user_id = ?');
            $stmt->execute([$_POST['user_id']]);
            if ($stmt->rowCount() > 0) {
                echo 'User', $_POST['user_id'], ' wurde erfolgreich gelöscht';
            } else {
                echo 'Löschen fehlgeschlagen';
            }
        } else if (isset($_POST['update'])) {
            $stmt = $pdo->prepare('SELECT * FROM user WHERE user_id = ?');
            $stmt->execute([$_POST['user_id']]);
            $user = $stmt->fetch();

            if(isset($_POST['user_password'])){
                $password = $_POST['user_password'];
                $hashedPassword = hash('sha256', $password);
                $user['user_password'] = $hashedPassword;
            }

            $user['user_name'] = $_POST['user_name'] ?: $user['user_name'];
            $user['user_score'] = $_POST['user_score'] ?: $user['user_score'];
            $user['user_x'] = $_POST['user_x'] ?: $user['user_x'];
            $user['user_y'] = $_POST['user_y'] ?: $user['user_y'];
            $user['user_character'] = $_POST['user_character'] ?: $user['user_character'];
            $user['user_inventory'] = $_POST['user_inventory'] ?: $user['user_inventory'];


            $stmt = $pdo->prepare('UPDATE user SET user_name = ?, user_score = ?, user_password = ?, user_x = ?,  user_y = ?, user_character = ?, user_inventory = ? WHERE user_id = ?');
            $stmt->execute([
                $user['user_name'],
                $user['user_score'],
                $user['user_password'],
                $user['user_x'],
                $user['user_y'],
                $user['user_character'],
                $user['user_inventory'],
                $user['user_id']
            ]);

            if ($stmt->rowCount() > 0) {
                echo 'User ', $_POST['user_id'], ' wurde erfolgreich bearbeitet.';
            } else {
                echo 'Bearbeiten fehlgeschlagen.';
            }
        }
    }

    $stmt = $pdo->query('SELECT * FROM user');
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);

    ?>
</head>

<body>

    <div class="scrollContainer">
        <div class="container">
            <h1 class="text-2xl mb-4">Nutzer-Verwaltung</h1>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Score</th>
                        <th>Position</th>
                        <th>Character</th>
                        <th>Inventar</th>
                        <th>Warteschlange der Aktionen</th>
                        <th>Erster Login</th>
                        <th>Letzter Login</th>
                        <th>Logins</th>
                        <th>Aktionen</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($users as $user): ?>
                        <tr>
                            <td><?= $user['user_id'] ?></td>
                            <td><?= htmlspecialchars($user['user_name']) ?></td>
                            <td><?= $user['user_score'] ?></td>
                            <td><?= $user['user_x'], $user['user_y'] ?></td>
                            <td><img src="getAsset.php?action=getImg&type=character&id=<?= $user['user_character'] ?>" alt="Character <?= $user['user_character'] ?>" class="element"></td>
                            <td><?= htmlspecialchars($user['user_inventory']) ?></td>
                            <td><?= $user['user_queue'] ?></td>
                            <td><?= $user['user_first_login'] ?></td>
                            <td><?= $user['user_last_login'] ?></td>
                            <td><?= $user['user_login_count'] ?></td>
                            <td>
                                <button onclick="edit(<?= $user['user_id'] ?>)">Bearbeiten</button>
                                <button onclick="remove(<?= $user['user_id'] ?>)">Löschen</button>
                                <button onclick="startGame('<?= $user['user_token'] ?>')">Fernsteuern</button>
                            </td>
                        </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>

            <div id="edit_popup" class="popup">
                <button class="close-btn" onclick="closePopup()">Schließen</button>
                <h2>Benutzer bearbeiten</h2>
                <form id="edit_form" method="POST">
                    <input type="hidden" id="edit_user_id" name="user_id">
                    <label for="user_name">Name:</label>
                    <input type="text" id="user_name" name="user_name" placeholder="no change"><br><br>
                    <label for="user_score">Score:</label>
                    <input type="number" id="user_score" name="user_score" placeholder="no change"><br><br>
                    <label for="user_password">Passwort:</label>
                    <input type="text" id="user_password" name="user_password" placeholder="no change"><br><br>
                    <label for="user_character">Character:</label>
                    <input type="number" id="user_characer" name="user_character" placeholder="no change"><br><br>
                    <label for="user_x">Position:</label>
                    <input type="number" id="user_x" name="user_x" placeholder="no change">
                    <input type="number" id="user_y" name="user_y" placeholder="no change"><br><br>
                    <label for="user_inventory">Inventory:</label>
                    <input type="text" id="user_inventory" name="user_inventory" placeholder="no change"><br><br>
                    <button type="submit" name="update">Speichern</button>
                </form>
            </div>

            <div id="delete_popup" class="popup">
                <button class="close-btn" onclick="closePopup()">Schließen</button>
                <h2>Benutzer Löschen</h2>
                <form id="delete_form" method="POST">
                    <input type="hidden" id="delete_user_id" name="user_id">
                    <label for="delete">Bestätigen Sie:</label>
                    <button type="submit" name="delete">Jetzt löschen</button>
                </form>
            </div>

            <div id="popup-overlay" class="popup-overlay"></div>

            <script>
                // Pop-Up anzeigen
                function edit(userId) {
                    document.getElementById('edit_popup').style.display = 'block';
                    document.getElementById('popup-overlay').style.display = 'block';
                    document.getElementById('edit_user_id').value = userId;
                }

                function remove(userId) {
                    document.getElementById('delete_popup').style.display = 'block';
                    document.getElementById('popup-overlay').style.display = 'block';
                    document.getElementById('delete_user_id').value = userId;
                }

                function startGame(token){
                    window.location.href = "../GUI/?token=" + token;
                }

                // Pop-Up schließen
                function closePopup() {
                    document.getElementById('edit_popup').style.display = 'none';
                    document.getElementById('delete_popup').style.display = 'none';
                    document.getElementById('popup-overlay').style.display = 'none';
                }
            </script>
        </div>
    </div>
</body>

</html>