package server;

import spieler.Spieler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HttpServer extends Datenbank {
    private ServerSocket socket;
    private int port;
    private ArrayList<Spieler> spieler;

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
        try {
            Socket client = this.socket.accept();
            if (client.isConnected()) {
                new Thread(() -> {
                    this.spieler.add(new Spieler(client));
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
