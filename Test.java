
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
        HttpServer server = new HttpServer();
        server.start();
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
