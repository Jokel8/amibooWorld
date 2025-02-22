package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.stream.Stream;

public class UserUpdaterServer extends HttpServer {
    public UserUpdaterServer(int port) {
        super(port);
    }

    @Override
    public void anfrageVerarbeiten(String anfrage, HashMap<String, String> parameter, Socket client, BufferedReader in) {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            StringBuilder antwort = new StringBuilder();
            switch (anfrage) {
                case "enterpos" -> {

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getBody(BufferedReader in) {
        StringBuilder queue = new StringBuilder();
        Stream<String> lines = in.lines();
        in.lines().forEach(queue::append);
        System.out.println(queue.toString());
        String[] headerbody = queue.toString().split("(?s)\r?\n\r?\n", 2);
        if (headerbody.length == 2) {
            return headerbody[1];
        } else {
            return null;
        }
    }
}
