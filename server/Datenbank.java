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

    private String dbGetTileAndMakeItIntoJson(int[][] tiles) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://v073086.kasserver.com/d0421573", "d0421573", "pZuw7TVdwLCqWUjMUD8o")) {
            // SQL-Abfrage, die den field_type basierend auf den Koordinaten sucht


            StringBuilder query = new StringBuilder();
            for (int i = 0; i < tiles.length; i++) {

                // Schleife durch das zweidimensionale Array der Koordinaten
                int x = tiles[i][0];
                int y = tiles[i][1];

                query.append("select field_type from map where field_x = " + x + "and field_y = " + y+";\n");
            }

            PreparedStatement pstmt = conn.prepareStatement(query.toString());

            // Führe die Abfrage aus
            ResultSet rs = pstmt.executeQuery();

            rs.toString();

            return null;

    }

}
