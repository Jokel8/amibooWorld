
import server.HttpServer;

import java.sql.SQLException;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        HttpServer server = new HttpServer();
        server.start();

//        System.out.println(Arrays.asList(Spieler.getParameter("http://example.com/page?parameter=value&also=another")));

//        JSONObject properties = new JSONObject();
//        properties.put("value", 3);
//        properties.put("name", "Gras");
//        properties.put("is_walkable", true);
//        properties.put("is_simmable", false);
//        JSONObject properties_obj = new JSONObject();
//        properties_obj.put("properties", properties);
//        JSONArray map = new JSONArray();
//        JSONObject tile = new JSONObject();
//        tile.put("x", 0);
//        tile.put("y", 0);
//        tile.put("properties", properties_obj);
//        map.put(tile);
//        JSONObject json = new JSONObject();
//        json.put("map", map);
//        System.out.println(json);

//        Datenbank datenbank = new Datenbank();
//        datenbank.dbConnect();
//        System.out.println(datenbank.dbGetTileAndMakeItIntoJson(datenbank.welcheTileSollIchHolen(1,1,1)));
    }
}
