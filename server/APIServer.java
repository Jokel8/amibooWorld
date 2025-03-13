package server;

import economy.Gestein;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
                    String tilesString;
                    if (parameter.containsKey("r")) {
                        tilesString = this.datenbank.dbGetTileAndMakeItIntoJson(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), Integer.parseInt(parameter.get("r")));
                    } else {
                        tilesString = this.datenbank.dbGetTileAndMakeItIntoJson(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 4);
                    }
                    //http header
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + tilesString.length() + "\n\n");
                    antwort.append(tilesString);
                }
                case "inventory" -> {
                    Inventory inventory = this.datenbank.getInventar(parameter.get("token"));
                    String inv = inventory.listItems();
                    System.out.println(inv);
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
//                case "login" -> {
//                    String token = parameter.get("token");
//                    this.spieler.addSpieler(new Spieler(this.datenbank.getQueue(token), this.datenbank.getInventar(token), token));
//                    antwort.append("HTTP/1.1 204 No Content\n");
//                }
                case "mine" -> {
                    this.abbauen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 0, parameter.get("token"));
                    antwort.append("HTTP/1.1 204 No Content\n");
                }
                case "build" -> {
                    antwort.append("HTTP/1.1 204 No Content\n");
                    this.datenbank.setFeld(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 0, 0, Integer.parseInt(parameter.get("type")));
                    this.bauen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), parameter.get("token"));
                }
                case "strike" -> {
                    antwort.append("HTTP/1.1 204 No Content\n");
                    this.datenbank.setFeld(this.datenbank.welcheTileSollIchHolen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 1), 0, 0, 6);
                }
                case "queue" -> {
                    queueSpeichern(in, parameter.get("token"));
                    antwort.append("HTTP/1.1 204 No Content\n");
                }
                case "characters" -> {
                    String html = this.datenbank.getCharacters();
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + html.length() + "\n\n");
                    antwort.append(html);
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
        //TODO fixen
        StringBuilder queue = new StringBuilder();
        List<String> lines = in.lines().toList();
        for (int i = 0; i < lines.size(); i++) {
            queue.append(lines.get(i));
        }
        String[] headerbody = queue.toString().split("\\R\\R", 2);
        if (headerbody.length == 2) {
            this.datenbank.updateMachen("UPDATE user SET user_queue = '" + headerbody[1] + "' WHERE user_token = " + token + ";");
            this.datenbank.updateMachen("UPDATE user SET user_queue_start = " + (long)(System.currentTimeMillis() / 1000) + " WHERE user_token = " + token + ";");
        }
        //this.spieler.setQueue(token, new Queue(headerbody[1], System.currentTimeMillis() / 1000L, 0.1));
    }
    private void abbauen(int x, int y, int radius, String token) {
        int[][] tiles = datenbank.welcheTileSollIchHolen(x, y, radius);
        int[] resourcen = datenbank.getResourcen(tiles);
        this.datenbank.setResourcen(tiles, 0, 0);
        Inventory inventory = this.datenbank.getInventar(token);
        inventory.addItem(new Holz(resourcen[0]));
        inventory.addItem(new Gestein(resourcen[1]));
        this.inventarAktualisieren(inventory, token);
    }
    private void bauen(int x, int y, String token) {
        //TODO kosten beachten
        ResultSet rs = this.datenbank.dbGetTiles(new int[][]{{x, y}});
        int kosten = this.datenbank.getKosten("haus_bauen");
        Inventory inventory = this.datenbank.getInventar(token);
        if (inventory.getMenge("gold") < kosten) {
            return;
        }
        if (rs != null) try {
            rs.next();
            int type = rs.getInt("field_type");
            if (type < 18) {
                this.datenbank.setFeld(x, y, 0, 0, 18);
                inventory.setMenge("gold", inventory.getMenge("gold") - kosten);
            } else if (type < 21) {
                this.datenbank.setFeld(x, y, 0, 0, type + 1);
                inventory.setMenge("gold", inventory.getMenge("gold") - kosten);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        inventarAktualisieren(inventory, token);
    }
    private void inventarAktualisieren(Inventory inventory, String token) {
        String query = "UPDATE user SET user_inventory = '" + inventory.toString() + "' WHERE user_token = '" + token + "';";
        this.datenbank.updateMachen(query);
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
