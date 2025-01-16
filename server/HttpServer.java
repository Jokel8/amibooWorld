package server;

import spieler.Spieler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpServer extends Datenbank {
    private ServerSocket socket;
    private int port;
    private ArrayList<Spieler> spieler;
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
    public HttpServer() {
        this.port  = 8080;
        this.spieler = new ArrayList<>();
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
                            HashMap<String, String> parameter = this.getParameter(anfrage);
                            anfrageVerarbeiten(anfrage, parameter);
                            while (!line.isEmpty()) {
                                System.out.println(in.readLine());
                            }

                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
    }

    private void anfrageVerarbeiten(String anfrage, HashMap<String, String> parameter) throws IOException {
        switch (anfrage) {
            case "/map.json" -> {
                System.out.println("Test erfolgreich!");
//                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
//                String antwort = datenbank.dbGetTileAndMakeItIntoJson()
//                        out.println(antwort);
//                this.close();
            }
        }
    }

    private String getAnfrage(String anfrage) {
        char[] chars = anfrage.toCharArray();
        String angerfage = "";
        int i = 0;
        for (; chars[i] != '/'; i++) {

        }
        for(; chars[i] != ' '; i++) {
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
        for (; i < chars.length; i++) {

            for (; chars[i] != '='; i++) {
                name.append(chars[i]);
            }
            i++;

            for (; chars[i] != '&' && i < chars.length; i++) {
                wert.append(chars[i]);
                if (i == chars.length - 1) {
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
