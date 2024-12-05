<?php
// Setze den Content-Type auf JSON
header('Content-Type: application/json');

// Kartengröße definieren
$mapWidth = 10;
$mapHeight = 10;

// Funktion zur Generierung von Tile-Eigenschaften
function generateTileProperties($value) {
    return [
        'value' => $value,
        'is_walkable' => $value <= 3, // Werte 1-3 sind begehbar
        'is_swimmable' => $value >= 4, // Werte 4-5 sind schwimmbar
        'is_flyable' => $value == 5   // Nur Wert 5 ist fliegbar
    ];
}

// Zufällige Karte generieren
$map = [];
for ($y = 0; $y < $mapHeight; $y++) {
    $row = [];
    for ($x = 0; $x < $mapWidth; $x++) {
        // Zufällige Zahl zwischen 1 und 5 generieren
        $randomValue = rand(1, 5);
        
        // Tile als Objekt mit Eigenschaften
        $tile = [
            'x' => $x,
            'y' => $y,
            'properties' => generateTileProperties($randomValue)
        ];
        
        $row[] = $tile;
    }
    $map[] = $row;
}

// Karte als JSON ausgeben
echo json_encode([
    'width' => $mapWidth,
    'height' => $mapHeight,
    'map' => $map
], JSON_PRETTY_PRINT);
?>