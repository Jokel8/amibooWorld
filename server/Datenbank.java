package server;

import java.sql.*;

public class Datenbank {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    private String driver;
    private String url;

    //private Json
    public Datenbank() {
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://localhost:3306/d0421573";
    }

    public int[][] welcheTileSollIchHolen(int x, int y, int radius) {
        int[][] tiles = new int[(radius+radius+1)*(radius+radius+1)][2];
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
    public void dbConnect () throws ClassNotFoundException, SQLException {
            Class.forName(driver);

            this.con = DriverManager.getConnection("jdbc:mysql://v073086.kasserver.com/d0421573","d0421573", "pZuw7TVdwLCqWUjMUD8o");
    }

    public String dbGetTileAndMakeItIntoJson(int[][] tiles) throws SQLException {
            // SQL-Abfrage, die den field_type basierend auf den Koordinaten sucht


            StringBuilder query = new StringBuilder();
            for (int i = 0; i < tiles.length; i++) {

                // Schleife durch das zweidimensionale Array der Koordinaten
                int x = tiles[i][0];
                int y = tiles[i][1];

                query.append("select field_type from map where field_x = " + x + "and field_y = " + y+";\n");
            }

            PreparedStatement pstmt = this.con.prepareStatement(query.toString());

            // Führe die Abfrage aus
            ResultSet rs = pstmt.executeQuery();

            System.out.println(rs.toString());

            return null;

    }
}
