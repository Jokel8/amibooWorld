package spieler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class Spieler {
    private Socket socket;
    private BufferedReader in;

    public Spieler(Socket socket) {
        this.socket = socket;
        try {

            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void close() {
        try {
            this.socket.close();
            this.in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void start() {

    }

    private void anfrageVerarbeiten(String anfrage) throws IOException {
        switch (anfrage) {
            case "/map.json" -> {
                System.out.println("Test erfolgreich!");
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                String antwort =
                out.println(antwort);
                this.close();
            }
        }
    }
}
