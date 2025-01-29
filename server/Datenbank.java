package server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class Datenbank {
    Connection con;

    private String driver;

    //private Json
    public Datenbank() {
        this.driver = "com.mysql.cj.jdbc.Driver";
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
    public boolean verbinden() {
        boolean verbunden = true;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            verbunden = false;
            System.out.println("Driver nicht gefunden");
        }

        try {
            this.con = DriverManager.getConnection("jdbc:mysql://v073086.kasserver.com/d0421573?allowMultiQueries=true","d0421573", "pZuw7TVdwLCqWUjMUD8o");
        } catch (SQLException e) {
            verbunden = false;
            System.out.println("Verbundung zur Datenbank konnte nicht hergestellt werden");
        }
        return verbunden;
    }

    public String dbGetTileAndMakeItIntoJson(int[][] tiles) {
        // SQL-Abfrage, die den field_type basierend auf den Koordinaten sucht


        StringBuilder query = new StringBuilder();
        query.append("SELECT type_name, type_is_walkable, type_is_swimmable, type_is_flyable FROM field_type JOIN map ON (map.field_type = field_type.type_id) WHERE ");
        for (int i = 0; i < tiles.length; i++) {

            // Schleife durch das zweidimensionale Array der Koordinaten
            int x = tiles[i][0];
            int y = tiles[i][1];

            //optional SELECT field_type FROM map WHERE field_x >= 0 AND field_x <= 2 AND field_y >= 0 AND field_y <=2;
            query.append("field_x = " + x + " AND field_y = " + y);
            if (i < tiles.length - 1) {
                query.append(" OR ");
            }
        }
        query.append(";");

        System.out.println(query);
        try {
            PreparedStatement pstmt = this.con.prepareStatement(query.toString());

            // Führe die Abfrage aus
            ResultSet rs = pstmt.executeQuery();

            JSONArray map = new JSONArray();

            for (int i = 0; rs.next(); i++) {
                JSONObject properties = new JSONObject();
                properties.put("value", 3);
                properties.put("name", rs.getString("type_name"));
                properties.put("is_walkable", rs.getInt("type_is_walkable"));
                properties.put("is_swimmable", rs.getInt("type_is_swimmable"));
                properties.put("is_flyable", rs.getInt("type_is_flyable"));

                JSONObject tile = new JSONObject();
                tile.put("x", tiles[i][0]);
                tile.put("y", tiles[i][1]);
                tile.put("properties", properties);
                map.put(tile);
            }

            JSONObject json = new JSONObject();
            json.put("width", (int) Math.sqrt(tiles.length));
            json.put("height", (int) Math.sqrt(tiles.length));
            json.put("map", map);

            return json.toString(4);
        } catch (SQLException e) {
            try {
                if (this.con.isClosed()) {
                    System.out.println("Datenbankverbindung getrennt");
                    if (this.verbinden()) {
                        return this.dbGetTileAndMakeItIntoJson(tiles);
                    } else {
                        throw new SQLException(e);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return "{\n\t\"fehler\": true\n}";
    }
}
