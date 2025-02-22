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

public abstract class HttpServer {
    private ServerSocket socket;
    private int port;
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

    public HttpServer(int port) {
        this.port = port;
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
                        //erste zeile des headers ausgeben
                        System.out.println(line);
                        if (line != null && isAnfrageValide(line)) {
                            String anfrage = this.getAnfrage(line);
                            //System.out.println(anfrage);
                            HashMap<String, String> parameter = this.getParameter(line);
                            anfrageVerarbeiten(anfrage, parameter, client, in);
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

    public abstract void anfrageVerarbeiten(String anfrage, HashMap<String, String> parameter, Socket client, BufferedReader in);

    private void viernullnull(Socket client) {
        try {
            new PrintWriter(client.getOutputStream(), true).println("HTTP/1.1 400 Bad Request\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAnfrage(String anfrage) {
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

    public HashMap<String, String> getParameter(String anfrage) {
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
}
