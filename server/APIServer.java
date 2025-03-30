package server;

import economy.resourcen.Stein;
import economy.resourcen.Gold;
import economy.resourcen.Holz;
import economy.Inventory;

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
import java.util.Random;

public class APIServer extends HttpServer {
    private Datenbank datenbank;
    private Random random;
    private double goldChance;

    public APIServer(int port) {
        super(port);
        this.datenbank = new Datenbank();
        this.datenbank.verbinden();
        this.random = new Random();
        this.goldChance = 0.75;
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
                    int id = this.datenbank.getID(parameter.get("token"));
                    Inventory inventory = this.datenbank.getInventar(id);
                    String inv = inventory.listItems();
                    //System.out.println(inv);
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
                    int id = this.datenbank.getID(parameter.get("token"));
                    this.abbauen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 0, id);
                    antwort.append("HTTP/1.1 204 No Content\n");
                }
                case "build" -> {
                    int id = this.datenbank.getID(parameter.get("token"));
                    antwort.append("HTTP/1.1 204 No Content\n");
                    this.bauen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), id);
                }
                case "strike" -> {
                    antwort.append("HTTP/1.1 204 No Content\n");
                    this.datenbank.setFeld(this.datenbank.welcheTileSollIchHolen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 1), 0, 0, 6);
                }
                case "startqueue" -> {
                    int id = this.datenbank.getID(parameter.get("token"));
                    String queueS = this.datenbank.getQueue(id);
                    Queue queue = new Queue(queueS, this, id);
                    queue.startQueue();
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
                case "setcharacter" -> {
                    int id = this.datenbank.getID(parameter.get("token"));
                    String key = parameter.get("key");
                    antwort.append(this.setCharacter(id, key));
                }
                case "character" -> {
                    int id = this.datenbank.getID(parameter.get("token"));
                    String charcter = "" + this.datenbank.getCharacter(id);
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + charcter.length() + "\n\n");
                    antwort.append(charcter);
                }
                case "trade" -> {
                    int id = this.datenbank.getID(parameter.get("token"));
                    int kaeufer = this.datenbank.getIDbyName(parameter.get("buyer"));
                    String item = parameter.get("item");
                    int menge = Integer.parseInt(parameter.get("menge"));
                    this.handel(kaeufer, id, item, menge);
                    antwort.append("HTTP/1.1 204 No Content\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n");
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
//    private void queueSpeichern(BufferedReader in, String token) {
//        //TODO fixen
//        StringBuilder queue = new StringBuilder();
//        List<String> lines = in.lines().toList();
//        for (int i = 0; i < lines.size(); i++) {
//            queue.append(lines.get(i));
//        }
//        JSONObject http = HTTP.toJSONObject(queue.toString());
//        System.out.println(http.toString(4));
//        String[] headerbody = queue.toString().split("\\R\\R", 2);
//        if (headerbody.length == 2) {
//            this.datenbank.updateMachen("UPDATE user SET user_queue = '" + headerbody[1] + "' WHERE user_token = " + token + ";");
//            this.datenbank.updateMachen("UPDATE user SET user_queue_start = " + (long)(System.currentTimeMillis() / 1000) + " WHERE user_token = " + token + ";");
//        }
//        //this.spieler.setQueue(token, new Queue(headerbody[1], System.currentTimeMillis() / 1000L, 0.1));
//    }


    // Vorerst nur dem Server verkaufen
    public void handel(int kaeufer_id, int verkaeufer_id, String Itemname, int menge) {
        Inventory inv = this.datenbank.getInventar(kaeufer_id);
        Inventory inv2 = this.datenbank.getInventar(verkaeufer_id);
        int kostenItem = datenbank.getKosten(Itemname);
        int kostenGold = datenbank.getKosten("gold");
        kaeufer_id = -1;
        int verkaufsObjekt;

        System.out.println(menge);

        if (menge > inv2.getMenge(Itemname)) {
            verkaufsObjekt = inv2.getMenge(Itemname);
        } else {
            verkaufsObjekt = menge;
        }
        System.out.println(verkaufsObjekt);


        int kosten = kostenItem * verkaufsObjekt;
        System.out.println(kosten);

        int totalGold = kosten / kostenGold;
        System.out.println(totalGold);


        int goldServer = inv.getMenge("gold");
        goldServer -= totalGold;

        int totalItem = (totalGold * kostenGold) / kostenItem;
        System.out.println(totalItem);
        inv.setMenge("gold", goldServer);
        inv2.getMenge("gold");
        inv2.setMenge("gold", inv2.getMenge("gold") + totalGold);
        inv2.setMenge(Itemname,(inv2.getMenge(Itemname)-totalItem));


        inventarAktualisieren(inv, kaeufer_id);
        inventarAktualisieren(inv2, verkaeufer_id);
    }


    public void bewegen(int x, int y, int id) {
        String query = "UPDATE user SET user_x = " + x + ", user_y = " + y + " WHERE user_id = " + id;
        this.datenbank.updateMachen(query);
    }

    public void abbauen(int x, int y, int radius, int id) {
        int[][] tiles = datenbank.welcheTileSollIchHolen(x, y, radius);
        int[] resourcen = datenbank.getResourcen(tiles);
        this.datenbank.setResourcen(tiles, 0, 0);
        Inventory inventory = this.datenbank.getInventar(id);
        inventory.addItem(new Holz(resourcen[0]));
        inventory.addItem(new Stein(resourcen[1]));
        if (this.random.nextDouble() <= goldChance) {
            inventory.addItem(new Gold(1));
        }
        this.inventarAktualisieren(inventory, id);
    }

    public void bauen(int x, int y, int id) {
        //TODO kosten beachten
        ResultSet rs = this.datenbank.dbGetTilesFast(x, y, 0);
        int kosten = this.datenbank.getKosten("haus_bauen");
        Inventory inventory = this.datenbank.getInventar(id);
        if (inventory.getMenge("gold") < kosten) {
            return;
        }
        String felder = "SELECT COUNT(*) AS total FROM map WHERE field_x = " + x + " AND field_y = " + y + ";";
        int felderCount = this.datenbank.getCount(felder);
        if (rs != null) try {
            rs.next();
            int type = rs.getInt("field_type");
            if (felderCount == 1) {
                this.datenbank.insertFeld(x, y, 0, 0, 100);
                inventory.setMenge("gold", inventory.getMenge("gold") - kosten);
            } else if (felderCount >= 2) {
                rs.next();
                type = rs.getInt("field_type");
                if (type < 104) {
                    this.datenbank.insertFeld(x, y, 0, 0, type + 1);
                    inventory.setMenge("gold", inventory.getMenge("gold") - kosten);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        inventarAktualisieren(inventory, id);
    }

    private void inventarAktualisieren(Inventory inventory, int id) {
        String query = "UPDATE user SET user_inventory = '" + inventory.toString() + "' WHERE user_id = " + id + ";";
        this.datenbank.updateMachen(query);
    }

    private String setCharacter(int id, String key) {
        String antwort;
        if (this.datenbank.setCharacters(id, key)) {
            antwort = "HTTP/1.1 204 No Content\n" +
                    "Content-Type: application/json\n" +
                    "Access-Control-Allow-Origin: *\n";
        } else {
            antwort = "HTTP/1.1 500 Internal Server Error\n";
        }
        return antwort;
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
