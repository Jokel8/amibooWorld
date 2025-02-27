package server;

import economy.Holz;
import economy.Inventory;

import javax.xml.datatype.Duration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.stream.Stream;

public class APIServer extends HttpServer {
    private Datenbank datenbank;
    private SichereVerketteteSpielerListe spieler;

    public APIServer(int port) {
        super(port);
        this.datenbank = new Datenbank();
        this.datenbank.verbinden();
        new Thread(() -> {
           spieler.speichern(this.datenbank);
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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
                    Spieler spieler = this.spieler.getSpieler(parameter.get("token"));
                    Inventory inventory = spieler.getInventory();
                    String inv = inventory.toString();
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
                    String token = parameter.get("token");
                    this.spieler.addSpieler(new Spieler(this.datenbank.getQueue(token), this.datenbank.getInventar(token), token));
                    antwort.append("HTTP/1.1 204 No Content\n");
                }
                case "mine" -> {
                    this.abbauen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 0, parameter.get("token"));
                    String inv = this.spieler.getSpieler(parameter.get("token")).getInventory().toString();
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + inv.length() + "\n\n");
                    antwort.append(inv);
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
        this.spieler.setQueue(token, new Queue(headerbody[1], System.currentTimeMillis() / 1000L, 0.1));
    }
    private void abbauen(int x, int y, int radius, String token) {
        int[][] tiles = datenbank.welcheTileSollIchHolen(x, y, radius);
        int[] resourcen = datenbank.getResourcen(tiles);
        this.datenbank.setResourcen(tiles, 0, 0);
        Inventory inventory = this.spieler.getSpieler(token).inventory;
        inventory.addItem(new Holz(resourcen[0]));
        inventory.addItem(new Holz(resourcen[1]));
        this.spieler.setInventory(token, inventory);
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
