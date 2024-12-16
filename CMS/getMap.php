<?php
// Datenbankverbindung einbinden
require_once 'dbConnect.php';

// Setze den Content-Type auf JSON
header('Content-Type: application/json');

try {
    // Datenbankverbindung herstellen
    $pdo = getDBConnection();

    // Kartendimensionen ermitteln
    $dimensionsStmt = $pdo->query("
        SELECT 
            MIN(field_x) as min_x, 
            MAX(field_x) as max_x, 
            MIN(field_y) as min_y, 
            MAX(field_y) as max_y 
        FROM map
    ");
    $dimensions = $dimensionsStmt->fetch(PDO::FETCH_ASSOC);

    // Kartendaten mit Feldeigenschaften laden
    $mapStmt = $pdo->query("
        SELECT 
            m.field_x, 
            m.field_y, 
            m.holz, 
            m.gold, 
            ft.type_id,
            ft.type_name,
            ft.type_img,
            ft.type_is_walkable,
            ft.type_is_swimmable,
            ft.type_is_flyable
        FROM map m
        JOIN field_type ft ON m.field_type = ft.type_id
        ORDER BY m.field_y, m.field_x
    ");
    $mapData = $mapStmt->fetchAll(PDO::FETCH_ASSOC);

    // Karte in 2D-Array umwandeln
    $map = [];
    $width = $dimensions['max_x'] - $dimensions['min_x'] + 1;
    $height = $dimensions['max_y'] - $dimensions['min_y'] + 1;

    foreach ($mapData as $tile) {
        $tile['properties'] = [
            'value' => $tile['type_id'],
            'name' => $tile['type_name'],
            'is_walkable' => (bool)$tile['type_is_walkable'],
            'is_swimmable' => (bool)$tile['type_is_swimmable'],
            'is_flyable' => (bool)$tile['type_is_flyable']
        ];
        
        $map[] = [
            'x' => $tile['field_x'],
            'y' => $tile['field_y'],
            'properties' => $tile['properties'],
            'resources' => [
                'holz' => $tile['holz'],
                'gold' => $tile['gold']
            ]
        ];
    }

    // JSON-Antwort generieren
    echo json_encode([
        'width' => $width,
        'height' => $height,
        'map' => $map
    ], JSON_PRETTY_PRINT);

} catch (Exception $e) {
    // Fehlerbehandlung
    echo json_encode([
        'error' => $e->getMessage()
    ]);
}
?>