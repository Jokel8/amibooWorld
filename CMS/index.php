<!DOCTYPE html>
<html lang="de">

<head>
    <meta charset="UTF-8">
    <title>Karten Generator</title>
    <link rel="stylesheet" href="cms.css" />
</head>

<body>
    <div class="nav-container container">
        <div class="header">Content Management System (CMS)</div>
        <form class="nav-bar" method="GET">
            <input class="nav-item" type="button" name="type" value="Assets Hinzufügen" onclick="toggleButton(this)" />
            <input class="nav-item" type="button" name="type" value="Assets Betrachten" onclick="toggleButton(this)" />
            <input class="nav-item" type="button" name="type" value="Nutzer verwalten" onclick="toggleButton(this)" />
            <input class="nav-item" type="button" name="type" value="Spiel testen" onclick="window.location.href = '../GUI/'" />
        </form>
    </div>

    <div id="content">
        <?php
            if (isset($_GET['selected'])) {
                $selected = explode(",", $_GET['selected']);
                if (in_array("Assets Hinzufügen", $selected)) {
                    include "uploader.html";
                }
                if (in_array("Assets Betrachten", $selected)) {
                    include "viewer.html";
                }
                if (in_array("Nutzer verwalten", $selected)) {
                    include "user.php";
                }
            }
        ?>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            let urlParams = new URLSearchParams(window.location.search);
            let selectedButtons = urlParams.get("selected") ? urlParams.get("selected").split(",") : [];
            document.querySelectorAll(".nav-item").forEach(button => {
                if (selectedButtons.includes(button.value)) {
                    button.classList.add("active");
                }
            });
        });

        function toggleButton(button) {
            button.classList.toggle("active");
            let selectedButtons = [];
            document.querySelectorAll(".nav-item.active").forEach(activeButton => {
                selectedButtons.push(activeButton.value);
            });
            let newUrl = window.location.pathname + "?selected=" + selectedButtons.join(",");
            window.location.href = newUrl;
        }
    </script>
    
</body>

</html>