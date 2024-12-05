<!DOCTYPE html>
<html lang="de">

<head>
    <meta charset="UTF-8">
    <title>Karten Generator</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>

<body class="bg-gray-100 p-8">
    <div class="container mx-auto bg-white p-6 rounded shadow">
        <h1 class="text-2xl mb-4">Feldtyp-Verwaltung</h1>

        <div class="grid grid-cols-2 gap-4">
            <!-- Feldtypen Liste -->
            <div>
                <h2 class="text-xl mb-2">Feldtypen</h2>
                <form id="fieldTypeForm" method="POST" class="mb-4">
                    <input type="hidden" name="action" value="add_type">
                    <div class="grid grid-cols-2 gap-2">
                        <input type="text" name="type_name" placeholder="Feldtyp Name" class="border p-2 rounded">
                        <input type="text" name="type_img" placeholder="Bild URL" class="border p-2 rounded">
                        <label>
                            <input type="checkbox" name="type_is_walkable"> Begehbar
                        </label>
                        <label>
                            <input type="checkbox" name="type_is_swimmable"> Schwimmbar
                        </label>
                        <label>
                            <input type="checkbox" name="type_is_flyable"> Fliegbar
                        </label>
                        <input type="number" step="0.01" name="probability" placeholder="Wahrscheinlichkeit" class="border p-2 rounded">
                        <button type="submit" class="bg-blue-500 text-white p-2 rounded">
                            Feldtyp hinzufügen
                        </button>
                    </div>
                </form>

                <table class="w-full border">
                    <thead>
                        <tr class="bg-gray-200">
                            <th>ID</th>
                            <th>Name</th>
                            <th>Bild</th>
                            <th>Begehbar</th>
                            <th>Durchschwimmbar</th>
                            <th>Überfliegbar</th>
                            <th>Wahrscheinlichkeit</th>
                            <th>Aktionen</th>
                        </tr>
                    </thead>
                    <tbody id="fieldTypeList">
                        <!-- Dynamische Feldtypen werden hier eingefügt -->
                    </tbody>
                </table>
            </div>

            <!-- Karten-Generator -->
            <div>
                <h2 class="text-xl mb-2">Feldtypen</h2>
                <h2 class="text-xl mb-2">Karten Generator</h2>
                <form id="mapGeneratorForm" method="POST">
                    <input type="hidden" name="action" value="generate_map">
                    <div class="grid grid-cols-2 gap-2">
                        <input type="number" name="map_width" placeholder="Kartenbreite" class="border p-2 rounded" value="10">
                        <input type="number" name="map_height" placeholder="Kartenhöhe" class="border p-2 rounded" value="10">
                        <input type="number" name="max_resources" placeholder="Max Rohstoffe" class="border p-2 rounded" value="100">
                        <button type="submit" class="bg-green-500 text-white p-2 rounded col-span-2">
                            Karte generieren
                        </button>
                    </div>
                </form>
                // In createMap.php, innerhalb des HTML-Formulars für den Karten-Generator:

                <button type="button" id="deleteMapBtn" class="bg-red-500 text-white p-2 rounded col-span-2">
                    Karte löschen
                </button>

                <script>
                    // Fügen Sie dieser Funktion hinzu
                    async function deleteMap() {
                        if (!confirm('Möchten Sie die gesamte Karte wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.')) {
                            return;
                        }

                        try {
                            const response = await fetch('deleteMap.php', {
                                method: 'POST'
                            });
                            const result = await response.json();

                            if (result.success) {
                                alert('Karte erfolgreich gelöscht');
                                // Optional: Seite neu laden oder Karte im Frontend aktualisieren
                                location.reload();
                            } else {
                                alert('Fehler beim Löschen: ' + result.message);
                            }
                        } catch (error) {
                            console.error('Fehler:', error);
                            alert('Ein unerwarteter Fehler ist aufgetreten');
                        }
                    }

                    // Event-Listener hinzufügen
                    document.getElementById('deleteMapBtn').addEventListener('click', deleteMap);
                </script>
            </div>
        </div>
    </div>

    <script>
        async function loadFieldTypes() {
            const response = await fetch('loadFieldTypes.php');
            const fieldTypes = await response.json();
            const list = document.getElementById('fieldTypeList');
            list.innerHTML = '';
            fieldTypes.forEach(type => {
                const row = document.createElement('tr');
                row.innerHTML = `
                <td>${type.type_id}</td>
                <td>${type.type_name}</td>
                <td>${type.type_img}</td>
                <td>${type.type_is_walkable}</td>
                <td>${type.type_is_swimmable}</td>
                <td>${type.type_is_flyable}</td>
                <td>${type.probability}</td>
                <td>
                    <button onclick="deleteFieldType(${type.type_id})" 
                            class="bg-red-500 text-white p-1 rounded">Löschen</button>
                </td>
            `;
                list.appendChild(row);
            });
        }

        async function deleteFieldType(typeId) {
            const response = await fetch('loadFieldTypes.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=delete_type&type_id=${typeId}`
            });
            const result = await response.json();
            if (result.success) {
                loadFieldTypes();
            }
        }

        document.addEventListener('DOMContentLoaded', () => {
            loadFieldTypes();

            document.getElementById('fieldTypeForm').addEventListener('submit', async (e) => {
                e.preventDefault();
                const formData = new FormData(e.target);
                const response = await fetch('loadFieldTypes.php', {
                    method: 'POST',
                    body: formData
                });
                const result = await response.json();
                if (result.success) {
                    loadFieldTypes();
                    e.target.reset();
                }
            });

            document.getElementById('mapGeneratorForm').addEventListener('submit', async (e) => {
                e.preventDefault();
                const formData = new FormData(e.target);
                const response = await fetch('generateMap.php', {
                    method: 'POST',
                    body: formData
                });
                const result = await response.json();
                if (result.success) {
                    alert('Karte generiert!');
                }
            });
        });
    </script>
</body>

</html>