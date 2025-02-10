<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <?php
        $user = $_POST["user"];
        $password = $_POST["password"];
        $password_v = $_POST["password_v"];
        $class = $_POST["class"];
        
        $host_name = '10.8.175.195';
        $user_name = 'schueler';
        $password_s = 'Programmieren';
        $database = 'willigis_game';

        $conn = mysqli_connect($host_name, $user_name, $password_s, $database);

        if (!$conn) {
            die("Verbindung nicht möglich oder so, Error: " . mysqli_connect_error());
        }
        echo "Verbunden ";
        if($password == $password_v) {
            $password = md5($password);
            $sql = "INSERT INTO spieler (user, password, class) VALUES ('$user', '$password', $class)";
            if (mysqli_query($conn, $sql)) {
                echo "Erfolg!";
                header("Location: /admin/dataaccess/add.html?Erfolg");
            }
        }
        else {
            echo "Passworter ungleich";
        }
    ?>
</body>
</html>