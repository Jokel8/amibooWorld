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
        //liste der benötigten tiles erstellen
        int[][] tiles = new int[(radius+radius+1)*(radius+radius+1)][2];
        int i = 0;
        for (int tileX = x - radius; tileX <= x + radius; tileX++ ) {
            for (int tileY = y - radius; tileY <= y + radius; tileY++ ) {
                //macht das es loopt
                tiles[i] = new int[]{tileX % 1000, tileY % 1000};
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
        //sql abfrage erstellen
        StringBuilder query = new StringBuilder();
        query.append("SELECT type_name, type_is_walkable, type_is_swimmable, type_is_flyable, holz, gold FROM field_type JOIN map ON (map.field_type = field_type.type_id) WHERE ");
        for (int i = 0; i < tiles.length; i++) {

            int x = tiles[i][0];
            int y = tiles[i][1];

            query.append("field_x = " + x + " AND field_y = " + y);
            if (i < tiles.length - 1) {
                query.append(" OR ");
            }
        }
        query.append(";");

        //System.out.println(query);
        try {
            PreparedStatement pstmt = this.con.prepareStatement(query.toString());

            // Führe die Abfrage aus
            ResultSet rs = pstmt.executeQuery();

            JSONArray map = new JSONArray();

            //tiles in json umwandeln
            for (int i = 0; rs.next(); i++) {
                JSONObject properties = new JSONObject();
                properties.put("value", 3);
                properties.put("name", rs.getString("type_name"));
                properties.put("is_walkable", rs.getInt("type_is_walkable"));
                properties.put("is_swimmable", rs.getInt("type_is_swimmable"));
                properties.put("is_flyable", rs.getInt("type_is_flyable"));

                JSONObject resources = new JSONObject();
                resources.put("holz", rs.getInt("holz"));
                resources.put("gold", rs.getInt("gold"));

                JSONObject tile = new JSONObject();
                tile.put("x", tiles[i][0]);
                tile.put("y", tiles[i][1]);
                tile.put("properties", properties);
                tile.put("resources", resources);
                map.put(tile);
            }

            JSONObject json = new JSONObject();
            json.put("width", (int) Math.sqrt(tiles.length));
            json.put("height", (int) Math.sqrt(tiles.length));
            json.put("map", map);

            return json.toString(4);
        } catch (SQLException e) {
            try {
                //verbindung zu datenbank prüfen und zur not wieder herstellen
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
