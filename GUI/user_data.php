<?php
header('Content-Type: application/json');

// Datenbank-Verbindungseinstellungen
$host = 'v073086.kasserver.com';
$db   = 'd0421573';
$user = 'd0421573';
$pass = 'pZuw7TVdwLCqWUjMUD8o';

// Sicherheits- und Validierungsfunktionen
function validateInput($input)
{
    $input = trim($input);
    $input = stripslashes($input);
    $input = htmlspecialchars($input);
    return $input;
}

try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $data = json_decode(file_get_contents('php://input'), true);

    if (isset($data['action']) && $data['action'] === 'register') {
        // Registrierungsprozess
        $name = validateInput($data['name']);
        $password = $data['password'];

        // Überprüfen, ob Name bereits existiert
        $stmt = $pdo->prepare('SELECT * FROM user WHERE user_name = ?');
        $stmt->execute([$name]);
        if ($stmt->fetch()) {
            echo json_encode(['success' => false, 'message' => 'Nutzername ist bereits vergeben']);
            exit;
        }

        // Benutzer in Datenbank einfügen
        $stmt = $pdo->prepare('INSERT INTO user (user_name, user_password) VALUES (?, ?)');
        $result = $stmt->execute([$name, $password]);

        if ($result) {
            echo json_encode(['success' => true]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Registrierung fehlgeschlagen']);
        }
    } else if ((isset($data['action']) && $data['action'] === 'login')) {
        // Login-Prozess
        $name = validateInput($data['name']);
        $password = validateInput($data['password']);

        // Benutzer in Datenbank suchen
        $stmt = $pdo->prepare('SELECT * FROM user WHERE user_name = ?');
        $stmt->execute([$name]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$user) {
            echo json_encode(['success' => false, 'message' => 'Dieser Nutzer exisistiert leider nicht']);
        } else {
            if ($password != $user['user_password']) {
                echo json_encode(['success' => false, 'message' => "Dieses Passwort ist leider falsch $password"]);
            } else {
                $token = bin2hex(random_bytes(20));
                $stmt = $pdo->prepare('UPDATE user SET user_token = ?, user_login_count = ?, user_last_login = curdate() WHERE user_name = ?');
                $stmt->execute([$token, $user['user_login_count'] + 1, $name]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);
                echo json_encode(['success' => true, 'token' => $token]);
            }
        }
    } else if (isset($data['action']) && $data['action'] === 'update') {
        $token = validateInput($data['token']);

        if ($data['update'] == "position") {
            $stmt = $pdo->prepare('UPDATE user SET user_x = ?, user_y = ? WHERE user_token = ?');
            $stmt->execute([validateInput($data['x']), validateInput($data['y']), validateInput($token)]);
            $userData = $stmt->fetch(PDO::FETCH_ASSOC);

            echo json_encode(['success' => true]);
        }
    } else if (isset($data['action']) && $data['action'] === 'get') {
        $token = validateInput($data['token']);

        $stmt = $pdo->prepare('SELECT * FROM user WHERE user_token = ? AND user_last_login = curdate()');
        $stmt->execute([validateInput($token)]);
        $userData = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$userData) {
            $stmt = $pdo->prepare('SELECT COUNT(*) FROM user WHERE user_token = ?');
            $stmt->execute([validateInput($token)]);
            $exists = $stmt->fetch(PDO::FETCH_ASSOC);

            if(!$exists) {
                echo json_encode(['success' => false, 'message' => "Token existiert nicht"]);
            } else {
                echo json_encode(['success' => false, 'message' => "Token abgelaufen"]);
            }
        } else {
            if ($data['get'] == "legitimation") {
                echo json_encode(['success' => true]);
            } else if ($data['get'] == "position") {
                echo json_encode(['success' => true, 'x' => $userData['user_x'], 'y' => $userData['user_y']]);
            } else if ($data['get'] == "inventory") {
                echo json_encode(['success' => true, 'inventory' => $userData['user_inventory']]);
            }
        }
    } else{
        echo json_encode(['success' => false, 'message' => 'Ungültige Anfrage']);
    }
} catch (PDOException $e) {
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
    error_log($e->getMessage());
}
