package server;

public class Datenbank {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    private String driver;
    private String url;

    //private Json
    public Datenbank() {
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://localhost:3306/d0421573";
    }
    private String[][] getMap(int x, int y, int radius) {
        int[][] tiles = welcheTileSollIchHolen(x, y, radius);
        return null;

    }
    private int[][] welcheTileSollIchHolen(int x, int y, int radius) {
        int[][] tiles = new int[radius+radius*radius+radius][2];
        int i = 0;
        for (int x_player = x - radius; x_player <= x + radius; x_player++ ) {
            for (int y_player = y - radius; y_player <= y + radius; y_player++ ) {
                tiles[i] = new int[]{x_player, y_player};
                i++;
            }
        }
        return tiles;
    }

    /**
     * Prozedur, um das Programm mit der Datenbank zu verknüpfen.
     *
     */
    private void dbConnect (){
        try {
            Class.forName(driver);

            Connection con = DriverManager.getConnection("jdbc:mysql://v073086.kasserver.com/d0421573","d0421573", "pZuw7TVdwLCqWUjMUD8o");
        } catch (SQLException | ClassNotFoundException e) {

        }
    }

    private int[][] dbGetTile(int[][] tiles) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://v073086.kasserver.com/d0421573", "d0421573", "pZuw7TVdwLCqWUjMUD8o")) {
            // SQL-Abfrage, die den field_type basierend auf den Koordinaten sucht
            String query = "SELECT field_type FROM fields WHERE x = ? AND y = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Schleife durch das zweidimensionale Array der Koordinaten
                for (int i = 0; i < tiles.length; i++) {
                    int x = tiles[i][0];
                    int y = tiles[i][1];

                    // Setze die Werte für x und y in der Abfrage
                    pstmt.setInt(1, x);
                    pstmt.setInt(2, y);

                    // Führe die Abfrage aus
                    try (ResultSet rs = pstmt.executeQuery()) {
                        // Wenn ein Datensatz gefunden wurde, hole den field_type
                        if (rs.next()) {
                            String fieldType = rs.getString("field_type");
                            System.out.println("Koordinate (" + x + ", " + y + ") hat field_type: " + fieldType);
                        } else {
                            System.out.println("Keine Daten für Koordinate (" + x + ", " + y + ") gefunden.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

}
