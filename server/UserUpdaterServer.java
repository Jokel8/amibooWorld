package server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.stream.Stream;

public class UserUpdaterServer extends HttpServer {
    public HashMap<String, Spieler> spieler;

    public UserUpdaterServer(int port) {
        super(port);
        this.spieler = new HashMap<>();
    }

    @Override
    public void anfrageVerarbeiten(String anfrage, HashMap<String, String> parameter, Socket client, BufferedReader in) {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            StringBuilder antwort = new StringBuilder();
            switch (anfrage) {
                case "enterplayer" -> {
                    String body = this.getBody(in);
                    JSONObject json = new JSONObject(body);
                    String token = json.getString("token");

                    if (spieler.containsKey(token)) {
                        antwort.append("HTTP/1.1 500 Internal Server Error\n");
                    } else {
                        spieler.put(token, new Spieler(token, json.getJSONObject("inventory"), json.getInt("geschwindigkeit"), json.getLong("start_zeit")));
                    }
                }
            }
            out.println(antwort.toString());
            out.close();
            this.close(client);
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
    private String getInventory(String token) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("GET /inventory?token=" + token + " HTTP/1.1");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return this.getBody(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
