<?php
require_once 'dbConnect.php';

header('Content-Type: application/json');

$action = $_POST['action'] ?? $_GET['action'] ?? null;

try {
    $pdo = getDBConnection();

    switch ($action) {
        case 'add_type':
            $stmt = $pdo->prepare("INSERT INTO field_type 
                (field_type_name, field_type_img, field_type_is_walkable, field_type_is_swimmable, field_type_is_flyable, field_type_probability) 
                VALUES (?, ?, ?, ?, ?, ?)");
            $stmt->execute([
                $_POST['field_type_name'] ?? '',
                $_POST['field_type_img'] ?? '',
                isset($_POST['field_type_is_walkable']) ? 1 : 0,
                isset($_POST['field_type_is_swimmable']) ? 1 : 0,
                isset($_POST['field_type_is_flyable']) ? 1 : 0,
                $_POST['field_type_probability'] ?? 1.0
            ]);
            echo json_encode(['success' => true]);
            break;

        case 'delete_type':
            $stmt = $pdo->prepare("DELETE FROM field_type WHERE field_type_id = ?");
            $stmt->execute([$_POST['field_type_id']]);
            echo json_encode(['success' => true]);
            break;

        default:
            $stmt = $pdo->query("
                SELECT field_type_id, 
                       field_type_name,
                       field_type_is_walkable,
                       field_type_is_swimmable,
                       field_type_is_flyable,
                       field_type_probability 
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