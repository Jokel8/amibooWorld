
import server.HttpServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.start();
    }
}
