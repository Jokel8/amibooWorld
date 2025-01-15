<?php
require_once 'dbConnect.php';

header('Content-Type: application/json');

$action = $_POST['action'] ?? $_GET['action'] ?? null;

try {
    $pdo = getDBConnection();

    switch ($action) {
        case 'add_type':
            $stmt = $pdo->prepare("INSERT INTO field_type 
                (type_name, type_img, type_is_walkable, type_is_swimmable, type_is_flyable, probability) 
                VALUES (?, ?, ?, ?, ?, ?)");
            $stmt->execute([
                $_POST['type_name'] ?? '',
                $_POST['type_img'] ?? '',
                isset($_POST['type_is_walkable']) ? 1 : 0,
                isset($_POST['type_is_swimmable']) ? 1 : 0,
                isset($_POST['type_is_flyable']) ? 1 : 0,
                $_POST['probability'] ?? 1.0
            ]);
            echo json_encode(['success' => true]);
            break;

        case 'delete_type':
            $stmt = $pdo->prepare("DELETE FROM field_type WHERE type_id = ?");
            $stmt->execute([$_POST['type_id']]);
            echo json_encode(['success' => true]);
            break;

        default:
            $stmt = $pdo->query("
                SELECT type_id, 
                       type_name,
                       type_is_walkable,
                       type_is_swimmable,
                       type_is_flyable,
                       probability 
                FROM field_type
            ");
            $fieldTypes = $stmt->fetchAll();
            echo json_encode($fieldTypes);
            break;
    }
} catch (Exception $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>