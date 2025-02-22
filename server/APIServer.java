package server;

import org.json.HTTPTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class APIServer extends HttpServer {
    private Datenbank datenbank;

    public APIServer(int port) {
        super(port);
        this.datenbank = new Datenbank();
        this.datenbank.verbinden();
    }

    @Override
    public void anfrageVerarbeiten(String anfrage, HashMap<String, String> parameter, Socket client, BufferedReader in) {
        //System.out.println(anfrage);
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            StringBuilder antwort = new StringBuilder();
            switch (anfrage) {
                case "map" -> {
                    int[][] tiles;
//                    if (parameter.containsKey("px") && parameter.containsKey("py")) {
//                        //tiles = this.datenbank.welcheTileSollIchHolen()
//                    } else {
                    if (parameter.containsKey("r")) {
                        tiles = this.datenbank.welcheTileSollIchHolen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), Integer.parseInt(parameter.get("r")));
                    } else {
                        tiles = this.datenbank.welcheTileSollIchHolen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 4);
                    }
                    String tilesString = this.datenbank.dbGetTileAndMakeItIntoJson(tiles);
                    //http header
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + tilesString.length() + "\n\n");
                    antwort.append(tilesString);
                }
                case "inventory" -> {
                    String inv = datenbank.getInventar(parameter.get("token"));
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + inv.length() + "\n\n");
                    antwort.append(inv);
                }
                //datenbank verbindung wieder herstellen
                case "db" -> {
                    if (this.datenbank.verbinden()) {
                        antwort.append("HTTP/1.1 204 No Content\n");
                    } else {
                        antwort.append("HTTP/1.1 500 Internal Server Error\n");
                    }
                }
                case "login" -> {
                    long token = this.datenbank.getToken(parameter.get("username"), getHash((parameter.get("password"))));
                    String tokenJson = "{\n\t\"token\": " + token + "\n}";
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + tokenJson.length() + "\n\n");
                    antwort.append(tokenJson);
                }
                case "mine" -> {
                    antwort.append("HTTP/1.1 204 No Content\n");
                    this.datenbank.setFeld(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 0, 0);
                }
                case "build" -> {
                    antwort.append("HTTP/1.1 204 No Content\n");
                    this.datenbank.setFeld(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 0, 0, Integer.parseInt(parameter.get("type")));
                }
                case "strike" -> {
                    antwort.append("HTTP/1.1 204 No Content\n");
                    this.datenbank.setFeld(this.datenbank.welcheTileSollIchHolen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 1), 0, 0, 6);
                }
                case "queue" -> {
                    queueSpeichern(in, parameter.get("token"));
                    antwort.append("HTTP/1.1 204 No Content\n");
                }
                default -> {
                    antwort.append("HTTP/1.1 404 Not Found\n");
                }
            }
            out.println(antwort.toString());
            out.close();
            this.close(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void queueSpeichern(BufferedReader in, String token) {
        StringBuilder queue = new StringBuilder();
        Stream<String> lines = in.lines();
        in.lines().forEach(queue::append);
        System.out.println(queue.toString());
        String[] headerbody = queue.toString().split("(?s)\r?\n\r?\n", 2);
        if (headerbody.length == 2) {
            this.datenbank.updateMachen("UPDATE user SET user_queue = '" + headerbody[1] + "' WHERE user_token = " + token + ";");
            this.datenbank.updateMachen("UPDATE user SET user_queue_start = " + (long)(System.currentTimeMillis() / 1000) + " WHERE user_token = " + token + ";");
        }
    }
    private String getHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, hash);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
