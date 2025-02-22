
import org.json.JSONArray;
import org.json.JSONObject;
import server.APIServer;
import server.HttpServer;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Hashtable;

public class Test {

    public static void main(String[] args) {
//        APIServer api = new APIServer(8080);
//        api.start();
        JSONObject queue = new JSONObject();
        JSONArray liste = new JSONArray();
        JSONObject pos = new JSONObject();
        pos.put("x", 0);
        pos.put("y", 0);
        liste.put(pos);
        liste.put(pos);
        liste.put(pos);

        queue.put("queue", liste);
        System.out.println(queue.toString(4));
//        SecureRandom random = new SecureRandom();
//        System.out.println(random.nextLong());
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            byte[] hash = md.digest("abc".getBytes());
//            BigInteger no = new BigInteger(1, hash);
//            String hashtext = no.toString(16);
//            while (hashtext.length() < 32) {
//                hashtext = "0" + hashtext;
//            }
//            System.out.println(hashtext);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
    }
}
