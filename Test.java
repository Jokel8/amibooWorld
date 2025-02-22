
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
        APIServer api = new APIServer(8080);
        api.start();
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
