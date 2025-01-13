import com.mysql.cj.xdevapi.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;
import server.HttpServer;

public class Test {

    public static void main(String[] args) {
        //HttpServer server = new HttpServer();
        //server.start();

        JSONObject properties = new JSONObject();
        properties.put("value", 3);
        properties.put("name", "Gras");
        properties.put("is_walkable", true);
        properties.put("is_simmable", false);
        JSONObject properties_obj = new JSONObject();
        properties_obj.put("properties", properties);
        JSONObject map = new JSONObject();
        map.put("x", 0);
        map.put("y", 0);
        map.put("properties", properties_obj);
        JSONObject json = new JSONObject();
        json.put("map", map);
        System.out.println(json);
    }
}
