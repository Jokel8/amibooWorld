package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpServer extends Datenbank {
    private ServerSocket socket;
    private int port;
    private Datenbank datenbank;

    public void start() {
        try {
            socket = new ServerSocket(port);
            while (true) {
                verbindungenAkzeptieren();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public HttpServer() throws SQLException, ClassNotFoundException {
        this.port  = 8080;
        this.datenbank = new Datenbank();
        this.datenbank.dbConnect();
    }
    private void verbindungenAkzeptieren() {

        new Thread(() -> {
            try {
                Socket client = this.socket.accept();

                if (client.isConnected()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String line = "";

                    line = in.readLine();
                    System.out.println(line);
                    String anfrage = this.getAnfrage(line);
                    System.out.println(anfrage);
                    HashMap<String, String> parameter = this.getParameter(line);
                    anfrageVerarbeiten(anfrage, parameter, client);
                    in.close();
                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void anfrageVerarbeiten(String anfrage, HashMap<String, String> parameter, Socket client) throws IOException, SQLException {
        System.out.println(anfrage);
        switch (anfrage) {
            case "map" -> {
                System.out.println("Test erfolgreich!");
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                int[][] tiles = this.datenbank.welcheTileSollIchHolen(Integer.parseInt(parameter.get("x")), Integer.parseInt(parameter.get("y")), 2);
                String antwort = this.datenbank.dbGetTileAndMakeItIntoJson(tiles);
                StringBuilder b = new StringBuilder();
                b.append("HTTP/1.1 200 OK\n" +
                        "Content-Type: text/html; charset=utf-8\n" +
                        "Content-Length: 55743\n" +
                        "Connection: keep-alive\n" +
                        "Cache-Control: s-maxage=300, public, max-age=0\n" +
                        "Content-Language: en-US\n" +
                        "Date: Thu, 06 Dec 2018 17:37:18 GMT\n" +
                        "ETag: \"2e77ad1dc6ab0b53a2996dfd4653c1c3\"\n" +
                        "Server: meinheld/0.6.1\n" +
                        "Strict-Transport-Security: max-age=63072000\n" +
                        "X-Content-Type-Options: nosniff\n" +
                        "X-Frame-Options: DENY\n" +
                        "X-XSS-Protection: 1; mode=block\n" +
                        "Vary: Accept-Encoding,Cookie\n" +
                        "Age: 7");
                b.append("<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        "\n" +
                        "<h4>");
                b.append(antwort);
                b.append("</h4>\n" +
                        "\n" +
                        "<p>My first paragraph.</p>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                out.println(b.toString());
                this.close(client);
            }
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
        char[] chars = anfrage.toCharArray();
        String angerfage = "";
        int i = 0;
        for (; chars[i] != '/'; i++) {

        }
        i++;
        for(; chars[i] != ' ' && chars[i] != '?'; i++) {
            angerfage += chars[i];
        }
        return angerfage;
    }
    private HashMap<String, String> getParameter(String anfrage) {
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

            System.out.println(name.toString() + " " + wert.toString());

            parameter.put(name.toString(), wert.toString());
            name = new StringBuilder();
            wert = new StringBuilder();
        }

        return parameter;
    }
}
