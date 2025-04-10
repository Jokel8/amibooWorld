package server;

import economy.Category;
import economy.Inventory;
import economy.Item;
import economy.Rarity;
import org.json.JSONArray;
import org.json.JSONObject;

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
        System.out.println("verbinden...");
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
        System.out.println("verbunden");
        return verbunden;
    }

    /**
     * sql abfrage machen
     * @param query sql befehl
     * @return result set muss noch auf != null geprüft werden
     */
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

    public ResultSet dbGetUserFast(int x, int y, int radius) {
        //query bauen
        StringBuilder query = new StringBuilder();
        query.append("SELECT user_step, user_id, user_x, user_y, user_action, user_action_time, user_character, user_name FROM user ");
        query.append("WHERE (");

        // X-coordinate conditions with boundary checks and wrapping logic
        if (x - radius >= 0 && x + radius <= 999) {
            query.append("user.user_x BETWEEN ").append(x - radius).append(" AND ").append(x + radius);
        } else {
            query.append("(");
            if (x - radius < 0) {
                query.append("user.user_x BETWEEN ").append(1000 + (x - radius)).append(" AND ").append(1000 - 1);
                query.append(" OR ");
            }
            query.append("user.user_x BETWEEN 0 AND ").append(x + radius);
            if (x + radius > 999) {
                query.append(" OR ");
                query.append("user.user_x BETWEEN 0 AND ").append(x + radius - 1000);
            }
            query.append(")");
        }

        // Y-coordinate conditions with boundary checks and wrapping logic
        query.append(" AND ");
        if (y - radius >= 0 && y + radius <= 999) {
            query.append("user.user_y BETWEEN ").append(y - radius).append(" AND ").append(y + radius);
        } else {
            query.append("(");
            if (y - radius < 0) {
                query.append("user.user_y BETWEEN ").append(1000 + (y - radius)).append(" AND ").append(1000 - 1);
                query.append(" OR ");
            }
            query.append("user.user_y BETWEEN 0 AND ").append(y + radius);
            if (y + radius > 999) {
                query.append(" OR ");
                query.append("user.user_y BETWEEN 0 AND ").append(y + radius - 1000);
            }
            query.append(")");
        }

        query.append(")");
        query.append(" ORDER BY user_last_login DESC LIMIT 100;");

        //System.out.println(query.toString());

        // Use prepared statement (assuming abfragMachen supports it)
        ResultSet rs = this.abfragMachen(query.toString());

        return rs;
    }

    public ResultSet dbGetTilesFast(int x, int y, int radius) {
        //query bauen
        StringBuilder query = new StringBuilder();
        query.append("SELECT field_x, field_y, field_type, field_type_name, field_type_is_walkable, field_type_is_swimmable, field_type_is_flyable, field_holz, field_gestein ");
        //query.append("SELECT * ");
        query.append("FROM field_type ");
        query.append("JOIN map ON map.field_type = field_type.field_type_id ");
        query.append("WHERE (");

        // X-coordinate conditions with boundary checks and wrapping logic
        if (x - radius >= 0 && x + radius <= 999) {
            query.append("map.field_x BETWEEN ").append(x - radius).append(" AND ").append(x + radius);
        } else {
            query.append("(");
            if (x - radius < 0) {
                query.append("map.field_x BETWEEN ").append(1000 + (x - radius)).append(" AND ").append(1000 - 1);
                query.append(" OR ");
            }
            query.append("map.field_x BETWEEN 0 AND ").append(x + radius);
            if (x + radius > 999) {
                query.append(" OR ");
                query.append("map.field_x BETWEEN 0 AND ").append(x + radius - 1000);
            }
            query.append(")");
        }

        // Y-coordinate conditions with boundary checks and wrapping logic
        query.append(" AND ");
        if (y - radius >= 0 && y + radius <= 999) {
            query.append("map.field_y BETWEEN ").append(y - radius).append(" AND ").append(y + radius);
        } else {
            query.append("(");
            if (y - radius < 0) {
                query.append("map.field_y BETWEEN ").append(1000 + (y - radius)).append(" AND ").append(1000 - 1);
                query.append(" OR ");
            }
            query.append("map.field_y BETWEEN 0 AND ").append(y + radius);
            if (y + radius > 999) {
                query.append(" OR ");
                query.append("map.field_y BETWEEN 0 AND ").append(y + radius - 1000);
            }
            query.append(")");
        }

        query.append(")");
        query.append("ORDER BY field_type ASC;");

        //System.out.println(query.toString());

        // Use prepared statement (assuming abfragMachen supports it)
        ResultSet rs = this.abfragMachen(query.toString());

        return rs;
    }


    public int[] getResourcen(int[][] tiles) {
        int[] resourcen = new int[2];
        ResultSet rs = this.dbGetTiles(tiles);

        if (rs != null) try {
            while (rs.next()) {
                int holz = rs.getInt("field_holz");
                int gestein = rs.getInt("field_gestein");
                resourcen[0] += holz;
                resourcen[1] += gestein;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resourcen;
    }
    public String dbGetTileAndMakeItIntoJson(int x, int y, int radius) {
        //ResultSet rs = this.dbGetTiles(tiles);
        ResultSet rs = this.dbGetTilesFast(x, y, radius);
        String json = this.tilesToJson(rs, radius);
        return json;
    }
    public String dbGetUsersAndMakeItIntoJson(int x, int y, int radius) {
        ResultSet rs = this.dbGetUserFast(x, y, radius);
        String json = this.usersToJson(rs, radius);
        return json;
    }

    public ResultSet dbGetTiles(int[][] tiles) {
        //sql abfrage erstellen
        StringBuilder query = new StringBuilder();
        query.append("SELECT field_x, field_y, field_type, field_type_name, field_type_is_walkable, field_type_is_swimmable, field_type_is_flyable, field_holz, field_gestein FROM field_type JOIN map ON (map.field_type = field_type.field_type_id) WHERE ");
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
        return rs;
    }
    private String tilesToJson(ResultSet rs, int radius) {
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
                tile.put("x", rs.getInt("field_x"));
                tile.put("y", rs.getInt("field_y"));
                tile.put("properties", properties);
                tile.put("resources", resources);
                map.put(tile);
            }

            JSONObject json = new JSONObject();
            json.put("width", radius+radius+1);
            json.put("height", radius+radius+1);
            json.put("map", map);

            return json.toString(4);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } else {
            return "{\n\t\"fehler\": true\n}";
        }
    }

    private String usersToJson(ResultSet rs, int radius) {
        if (rs != null) try {

            JSONArray users = new JSONArray();
            //        query.append("SELECT user_x, user_y, user_action, user_action_time, user_character, user_name FROM user ");
            //tiles in json umwandeln
            for (int i = 0; rs.next(); i++) {
                JSONObject user = new JSONObject();
                user.put("x", rs.getInt("user_x"));
                user.put("y", rs.getInt("user_y"));
                user.put("action", rs.getString("user_action"));
                user.put("action_time", rs.getInt("user_action_time"));
                user.put("username", rs.getString("user_name"));
                user.put("character", rs.getInt("user_character"));
                user.put("id", rs.getInt("user_id"));
                user.put("step", rs.getInt("user_step"));

                users.put(user);
            }

            JSONObject json = new JSONObject();
            json.put("users", users);

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
    public void insertFeld(int x, int y, int holz, int gestein, int type) {
        String delete = "DELETE FROM map WHERE field_x = "+x+" AND field_y = "+y+" AND field_type >= 100";
        updateMachen(delete);
        String query = "INSERT INTO map(field_x, field_y, field_gestein, field_holz, field_type) VALUES("+x+","+y+","+gestein+","+holz+","+type+");";
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
    public void setResourcen(int[][] tiles, int holz, int gestein) {
        //sql abfrage erstellen
        StringBuilder query = new StringBuilder();
        query.append("UPDATE map SET field_holz = " + holz + ", field_gestein = " + gestein + " WHERE ");
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

    public int getKosten(String item) {
        String query = "SELECT item_value FROM item WHERE item_name = '" + item + "';";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            rs.next();
            return rs.getInt("item_value");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Integer.MAX_VALUE;
    }

    /**
     * inventar aus der datenbank als json
     * @param id userid
     * @return json
     */
    public Inventory getInventar(int id) {
        String query = "SELECT user_inventory FROM user WHERE user_id = " + id + ";";
        ResultSet rs = this.abfragMachen(query);

        if (rs != null) try {
            rs.next();
            String inventar = rs.getString("user_inventory");
            return new Inventory(inventar);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Inventory("{\"fehler\": true}");
    }
    /**
     * update zur datenbank machen
     * @param query sql befehl
     */
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
    public String getQueue(int id) {
        String query = "SELECT user_queue FROM user WHERE user_id = " + id + ";";
        String queueS = "";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            rs.next();
            queueS = rs.getString("user_queue");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return queueS;
    }
    public void setCharacter(String username) {
        //String query = "SELECT"
    }
    public void resetQueue(int id) {
        String query = "UPDATE user SET user_queue = '{\"queue\":[]}' WHERE user_id = " + id;
        this.updateMachen(query);
    }
    public String getCharacters() {
        String query = "SELECT character_name FROM `character`;";
        ResultSet rs = this.abfragMachen(query);
        StringBuilder html = new StringBuilder();

        if (rs != null) try {
            while (rs.next()) {
                html.append("<option>" + rs.getString("character_name") + "</option>\n");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return html.toString();
    }
    public int getCharacter(int id) {
        String query = "SELECT user_character FROM user WHERE user_id = " + id + ";";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            rs.next();
            return rs.getInt("user_character");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
    public boolean setCharacters(int id, String key) {
        //nach charcter suchen
        String query = "SELECT COUNT(amiibo_character_id) AS erfolg, amiibo_character_id FROM amiibo WHERE amiibo_eingeloest = 0 AND amiibo_key = '" + key + "';";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            rs.next();
            if (rs.getInt("erfolg") == 1) {
                int character = rs.getInt("amiibo_character_id");
                //charcter aktualisieren
                query = "UPDATE user SET user_character = " + character + " WHERE user_id = " + id + ";";
                this.updateMachen(query);
                //auf eingeloest setztem
                query = "UPDATE amiibo SET amiibo_eingeloest = 1 WHERE amiibo_key = '" + key + "';";
                this.updateMachen(query);
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public int getCount(String query) {
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try{
            rs.next();
            return rs.getInt("total");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    public int getID(String token) {
        int id = -1;
        String query = "SELECT user_id FROM user WHERE user_token = '" + token + "';";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            rs.next();
            id = rs.getInt("user_id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
    public int getIDbyName(String name) {
        int id = -1;
        String query = "SELECT user_id FROM user WHERE user_name = '" + name + "';";
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            rs.next();
            id = rs.getInt("user_id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
    public String getAction(int id) {
        String query = "SELECT user_action FROM user WHERE user_id = " + id;
        ResultSet rs = this.abfragMachen(query);

        if (rs != null) try {
            rs.next();
            return rs.getString("user_action");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public void setAction(String action, int time, int id) {
        String query = "UPDATE user SET user_action = '" + action + "', user_action_time = " + time + " WHERE user_id = " + id;
        this.updateMachen(query);
    }
    public int getHealth(int id) {
        String query = "SELECT user_health FROM user WHERE user_id = " + id;
        ResultSet rs = this.abfragMachen(query);
        if (rs != null) try {
            rs.next();
            return rs.getInt("user_health");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    public void setUserStep(int step, int id) {
        String query = "UPDATE user SET user_step = " + step + " WHERE user_id = " + id;
        this.updateMachen(query);
    }
}
