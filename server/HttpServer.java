package server;

import economy.Inventory;

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

public class HttpServer extends Datenbank {
    private ServerSocket socket;
    private int port;
    private Datenbank datenbank;
    private String regex;
    private Pattern pattern;

    public void start() {
        try {
            this.socket = new ServerSocket(port);

            while (true) {
                verbindungenAkzeptieren();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpServer() {
        this.port = 8080;
        this.datenbank = new Datenbank();
        this.datenbank.verbinden();
        this.regex = "GET /[a-z]*((\\?[a-z]+=([a-z]|[0-9])+)(&[a-z]+=([a-z]|[0-9])+)*)?\\sHTTP/1\\.1";
        this.pattern = Pattern.compile(regex);
    }

    private void verbindungenAkzeptieren() {
        try {
            Socket client = this.socket.accept();

            if (client.isConnected()) {
                new Thread(() -> {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String line = "";
                        line = in.readLine();
                        //erste zeile des headers aufgeben
                        System.out.println(line);
                        if (line != null && isAnfrageValide(line)) {
                            String anfrage = this.getAnfrage(line);
                            //System.out.println(anfrage);
                            HashMap<String, String> parameter = this.getParameter(line);
                            anfrageVerarbeiten(anfrage, parameter, client);
                        } else {
                            System.out.println("^abgelehnt^");
                            viernullnull(client);
                        }
                        in.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isAnfrageValide(String anfrage) {
        Matcher matcher = this.pattern.matcher(anfrage);
        return matcher.matches();
    }

    private void anfrageVerarbeiten(String anfrage, HashMap<String, String> parameter, Socket client) {
        //System.out.println(anfrage);
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            StringBuilder antwort = new StringBuilder();
            switch (anfrage) {
                case "map" -> {
                    int[][] tiles = this.datenbank.welcheTileSollIchHolen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 2);
                    String tilesString = this.datenbank.dbGetTileAndMakeItIntoJson(tiles);
                    //http header
                    antwort.append("HTTP/1.1 200 OK\n" +
                            "Content-Type: application/json\n" +
                            "Access-Control-Allow-Origin: *\n" +
                            "Content-Length: " + tilesString.length() + "\n\n");
                    antwort.append(tilesString);
                }
                case "inventory" -> {
                    Inventory inventar = new Inventory(parameter.get("username"));
                    String inv = inventar.listItems();
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
    private void viernullnull(Socket client) {
        try {
            new PrintWriter(client.getOutputStream(), true).println("HTTP/1.1 400 Bad Request\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void close(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAnfrage(String anfrage) {
        //aus dem header das angefragte verzeichniss extrahieren
        char[] chars = anfrage.toCharArray();
        String angerfage = "";
        int i = 0;
        for (; chars[i] != '/'; i++) {

        }
        i++;
        for (; chars[i] != ' ' && chars[i] != '?'; i++) {
            angerfage += chars[i];
        }
        return angerfage;
    }

    private HashMap<String, String> getParameter(String anfrage) {
        if (!anfrage.contains("?")) {
            return null;
        }
        //aus dem header die parameter extrahieren
        char[] chars = anfrage.toCharArray();
        StringBuilder name = new StringBuilder();
        StringBuilder wert = new StringBuilder();
        HashMap parameter = new HashMap();
        int i = 0;

        for (; chars[i] != '?'; i++) {
        }
        i++;
        for (; i < chars.length - 9; i++) {

            for (; chars[i] != '='; i++) {
                name.append(chars[i]);
            }
            i++;
            for (; chars[i] != '&'; i++) {
                wert.append(chars[i]);
                if (i == chars.length - 10) {
                    break;
                }
            }
            //System.out.println(name.toString() + " " + wert.toString());
            parameter.put(name.toString(), wert.toString());
            name = new StringBuilder();
            wert = new StringBuilder();
        }

        return parameter;
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
