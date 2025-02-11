<?php
//header('Content-Type: application/json');

// Datenbank-Verbindungseinstellungen
$host = 'v073086.kasserver.com';
$db   = 'd0421573';
$user = 'd0421573';
$pass = 'pZuw7TVdwLCqWUjMUD8o';

// $login_name = $_POST["login-name"];
// $login_password = $_POST["login-password"];
// $register_name = $_POST["register-name"];
// $register_password = $_POST["register-password"];
// $register_confirm_password = $_POST["register-confirm-password"];
//
// $conn = mysqli_connect($host, $user, $pass, $db);
//
// if (!$conn) {
//     die("Verbindung nicht möglich oder so, Error: " . mysqli_connect_error());
// }
//
// if
//
// if($password == $password_v) {
//     $password = md5($password);
//     $sql = "INSERT INTO spieler (user, password, class) VALUES ('$user', '$password', $class)";
//     if (mysqli_query($conn, $sql)) {
//         echo("erfolg");
//     }
// }
// else {
//     echo("fehler");
// }
//
// ?>
Sicherheits- und Validierungsfunktionen
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
    } else {
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
                $stmt = $pdo->prepare('UPDATE user SET user_token = ? WHERE user_name = ?');
                $stmt->execute([$token, $name]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);
                echo json_encode(['success' => true, 'token' => $token]);
            }
        }
    }
} catch (PDOException $e) {
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
    error_log($e->getMessage());
}
