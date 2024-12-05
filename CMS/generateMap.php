<?php
require_once 'dbConnect.php';

header('Content-Type: application/json');

try {
    $pdo = getDBConnection();

    // Hole Feldtypen mit Wahrscheinlichkeiten
    $stmt = $pdo->query("SELECT * FROM field_type");
    $fieldTypes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Normalisiere Wahrscheinlichkeiten
    $totalProbability = array_sum(array_column($fieldTypes, 'probability'));
    $normalizedTypes = array_map(function($type) use ($totalProbability) {
        $type['normalized_probability'] = $type['probability'] / $totalProbability;
        return $type;
    }, $fieldTypes);

    // Generiere Karte
    $mapWidth = intval($_POST['map_width'] ?? 10);
    $mapHeight = intval($_POST['map_height'] ?? 10);
    $maxResources = intval($_POST['max_resources'] ?? 100);

    $pdo->beginTransaction();

    // Lösche alte Kartendaten
    $pdo->exec("TRUNCATE TABLE map");

    // Kartengenerierung
    for ($y = 0; $y < $mapHeight; $y++) {
        for ($x = 0; $x < $mapWidth; $x++) {
            // Wähle Feldtyp basierend auf Wahrscheinlichkeit
            $random = mt_rand() / mt_getrandmax();
            $cumulativeProbability = 0;
            $selectedType = null;

            foreach ($normalizedTypes as $type) {
                $cumulativeProbability += $type['normalized_probability'];
                if ($random <= $cumulativeProbability) {
                    $selectedType = $type;
                    break;
                }
            }

            // Generiere Ressourcen
            $holz = mt_rand(0, $maxResources / 2);
            $gold = mt_rand(0, $maxResources / 2);

            // Füge Feld zur Karte hinzu
            $stmt = $pdo->prepare("INSERT INTO map (field_x, field_y, field_type, holz, gold) VALUES (?, ?, ?, ?, ?)");
            $stmt->execute([$x, $y, $selectedType['type_id'], $holz, $gold]);
        }
    }

    $pdo->commit();
    echo json_encode(['success' => true]);

} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(['error' => $e->getMessage()]);
}
?>