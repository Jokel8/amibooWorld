<?php
require_once 'db_connection.php';

header('Content-Type: application/json');

try {
    $pdo = getDBConnection();

    // Alle Datensätze in der map-Tabelle löschen
    $deleteMapStmt = $pdo->prepare("TRUNCATE TABLE map");
    $deleteMapStmt->execute();

    // Optional: Log-Eintrag erstellen
    $logStmt = $pdo->prepare("
        INSERT INTO map_deletion_log (deletion_time, total_tiles_deleted) 
        VALUES (NOW(), (SELECT COUNT(*) FROM map))
    ");
    $logStmt->execute();

    echo json_encode([
        'success' => true,
        'message' => 'Karte erfolgreich gelöscht'
    ]);

} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}
?>