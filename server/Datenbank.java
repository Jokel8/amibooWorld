package server;

import economy.Category;
import economy.Inventory;
import economy.Item;
import economy.Rarity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.*;

public class Datenbank {
    private Connection con;

    private String driver;

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
                tiles[i] = new int[]{Math.floorMod(tileX, 1000), Math.floorMod(tileY, 1000)};
                i++;
            }
        }
        return tiles;
    }
//    public int[][] welcheTileSollIchHolen(int x, int y, int x_alt, int y_alt, int radius) {
//        int[][] xy = welcheTileSollIchHolen(x, y, radius);
//        int[][] xyAlt = welcheTileSollIchHolen(x, y, radius);
//        int minX = Math.min(xy[0][0], xyAlt[0][0]);
//        int minY = Math.min(xy[2*radius+1][1], xyAlt[2*radius+1][1]);
//        int maxXAlt = Math
//    }

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
    private ResultSet abfragMachen(String query) {

        try {
            PreparedStatement pstmt = this.con.prepareStatement(query.toString());
            // Führe die Abfrage aus
            ResultSet rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            try {
                //verbindung zu datenbank prüfen und zur not wieder herstellen
                if (this.con.isClosed()) {
                    System.out.println("Datenbankverbindung getrennt");
                    if (this.verbinden()) {
                        System.out.println("Datenbankverbindung hergestellt");
                        return this.abfragMachen(query);
                    } else {
                        throw new SQLException(e);
                    }
                } else {
                    throw new RuntimeException(e);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String dbGetTileAndMakeItIntoJson(int[][] tiles) {
        //sql abfrage erstellen
        StringBuilder query = new StringBuilder();
        query.append("SELECT field_type, field_type_name, field_type_is_walkable, field_type_is_swimmable, field_type_is_flyable, field_holz, field_gestein FROM field_type JOIN map ON (map.field_type = field_type.field_type_id) WHERE ");
        for (int i = 0; i < tiles.length; i++) {

            int x = tiles[i][0];
            int y = tiles[i][1];

            query.append("field_x = " + x + " AND field_y = " + y);
            if (i < tiles.length - 1) {
                query.append(" OR ");
            }
        }
        query.append(";");
        //System.out.println(query.toString());

        ResultSet rs = this.abfragMachen(query.toString());

        //System.out.println(query);
        if (rs != null) try {

            JSONArray map = new JSONArray();

            //tiles in json umwandeln
            for (int i = 0; rs.next(); i++) {
                JSONObject properties = new JSONObject();
                properties.put("value", rs.getInt("field_type"));
                properties.put("name", rs.getString("field_type_name"));
                properties.put("is_walkable", rs.getInt("field_type_is_walkable"));
                properties.put("is_swimmable", rs.getInt("field_type_is_swimmable"));
                properties.put("is_flyable", rs.getInt("field_type_is_flyable"));

                JSONObject resources = new JSONObject();
                resources.put("holz", rs.getInt("field_holz"));
                resources.put("gold", rs.getInt("field_gestein"));

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
            throw new RuntimeException(e);
        } else {
            return "{\n\t\"fehler\": true\n}";
        }
    }
    public long getToken(String username, String password) {
        String query = "SELECT user_token FROM user WHERE user_name = '" + username + "' AND user_password = '" + password + "'";
        ResultSet rs = this.abfragMachen(query);

        if (rs != null)try {
            if (rs.next()) {
                return rs.getLong("user_token");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } else {
            return 0;
        }
    }
    public void setFeld(int x, int y, int holz, int gestein) {
        String query = "UPDATE map SET field_holz = " + holz + ", field_gestein = " + gestein + " WHERE field_x = " + x + " AND field_y = " + y + ";";
        updateMachen(query);
    }
    public void setFeld(int x, int y, int holz, int gestein, int type) {
        String query = "UPDATE map SET field_holz = " + holz + ", field_gestein = " + gestein + ", field_type = " + type + " WHERE field_x = " + x + " AND field_y = " + y + ";";
        updateMachen(query);
    }
    public void setFeld(int[][] tiles, int holz, int gestein, int type) {
        //sql abfrage erstellen
        StringBuilder query = new StringBuilder();
        query.append("UPDATE map SET field_holz = " + holz + ", field_gestein = " + gestein + ", field_type = " + type + " WHERE ");
        for (int i = 0; i < tiles.length; i++) {

            int x = tiles[i][0];
            int y = tiles[i][1];

            query.append("field_x = " + x + " AND field_y = " + y);
            if (i < tiles.length - 1) {
                query.append(" OR ");
            }
        }
        query.append(";");
        updateMachen(query.toString());
    }
    public Inventory inventarEinlesen(String token) {
        Inventory inventory = new Inventory(token);
        String query = "SELECT user_inventory FROM user WHERE user_token = " + token + ";";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            while(rs.next()) {
                String inventar = rs.getString("user_inventory");
                JSONObject json = new JSONObject(inventar);
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    inventory.addItem(new Item(item.getString("name"), item.getBoolean("stackable"), item.getInt("value"), item.getEnum(Rarity.class, "rarity"), item.getString("description"), item.getString("manufacturer"), item.getEnum(Category.class, "category")));
                }
                inventory.addGold(json.getInt("toal_gold"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } else {
            Inventory fehler = new Inventory("fehler");
            fehler.addItem(new Item("fehler", false, 0, Rarity.UNCOMMON, "falscher token", "server", Category.OTHER));
            return fehler;
        }
        return inventory;
    }
    public String getInventar(String token) {
        String query = "SELECT user_inventoy FROM user WHERE user_token = " + token + ";";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null)try {
            return rs.getString("user_inventory");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "{\n\t\"token\": false\n}";
    }
    public void updateMachen(String query) {
        try {
            PreparedStatement pstmt = this.con.prepareStatement(query.toString());
            // Führe die Abfrage aus
            pstmt.executeUpdate();
        } catch (SQLException e) {
            try {
                //verbindung zu datenbank prüfen und zur not wieder herstellen
                if (this.con.isClosed()) {
                    System.out.println("Datenbankverbindung getrennt");
                    if (this.verbinden()) {
                        System.out.println("Datenbankverbindung hergestellt");
                        this.updateMachen(query);
                    } else {
                        throw new SQLException(e);
                    }
                } else {
                    throw new RuntimeException(e);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
