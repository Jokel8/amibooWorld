<!DOCTYPE html>
<html lang="de">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management</title>
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

            $user['user_name'] = $_POST['user_name'] ?: $user['user_name'];
            $user['user_score'] = $_POST['user_score'] ?: $user['user_score'];
            $user['user_gold'] = $_POST['user_gold'] ?: $user['user_gold'];
            $user['user_holz'] = $_POST['user_holz'] ?: $user['user_holz'];
            $user['user_inventory'] = $_POST['user_inventory'] ?: $user['user_inventory'];


            $stmt = $pdo->prepare('UPDATE user SET user_name = ?, user_score = ?, user_gold = ?, user_holz = ?, user_inventory = ? WHERE user_id = ?');
            $stmt->execute([
                $_POST['user_name'],
                $_POST['user_score'],
                $_POST['user_gold'],
                $_POST['user_holz'],
                $_POST['user_inventory'],
                $_POST['user_id']
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
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }

        th,
        td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #f4f4f4;
        }

        button {
            padding: 10px;
            cursor: pointer;
        }

        .popup {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: #fff;
            padding: 20px;
            border: 2px solid #ccc;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            z-index: 1000;
        }

        .popup-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 999;
            display: none;
        }

        .close-btn {
            background-color: #ff4c4c;
            color: #fff;
            border: none;
            padding: 5px 10px;
            cursor: pointer;
            float: right;
        }
    </style>
</head>

<body>
    <h1>User Management System</h1>

    <!-- Tabelle der Benutzer -->
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Score</th>
                <th>Gold</th>
                <th>Holz</th>
                <th>Inventar</th>
                <th>Spielzeit</th>
                <th>Letzter Login</th>
                <th>Logins</th>
                <th>Position</th>
                <th>Character</th>
                <th>Warteschlange der Aktionen</th>
                <th>Aktionen</th>
            </tr>
        </thead>
        <tbody>
            <?php foreach ($users as $user): ?>
                <tr>
                    <td><?= $user['user_id'] ?></td>
                    <td><?= htmlspecialchars($user['user_name']) ?></td>
                    <td><?= $user['user_score'] ?></td>
                    <td><?= $user['user_gold'] ?></td>
                    <td><?= $user['user_holz'] ?></td>
                    <td><?= htmlspecialchars($user['user_inventory']) ?></td>
                    <td><?= $user['user_playtime'] ?></td>
                    <td><?= $user['user_last_login'] ?></td>
                    <td><?= $user['user_login_count'] ?></td>
                    <td><?= $user['user_position'] ?></td>
                    <td><?= $user['user_character'] ?></td>
                    <td><?= $user['user_queue'] ?></td>
                    <td>
                        <button onclick="edit(<?= $user['user_id'] ?>)">Bearbeiten</button>
                        <button onclick="remove(<?= $user['user_id'] ?>)">Löschen</button>
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
            <label for="user_gold">Gold:</label>
            <input type="number" id="user_gold" name="user_gold" placeholder="no change"><br><br>
            <label for="user_holz">Holz:</label>
            <input type="number" id="user_holz" name="user_holz" placeholder="no change"><br><br>
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

        // Pop-Up schließen
        function closePopup() {
            document.getElementById('edit_popup').style.display = 'none';
            document.getElementById('delete_popup').style.display = 'none';
            document.getElementById('popup-overlay').style.display = 'none';
        }
    </script>

</body>

</html>