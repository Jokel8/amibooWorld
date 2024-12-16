package spieler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Spieler {
    private Socket socket;
    private BufferedReader in;

    public Spieler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            start();
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
        String line = "";
        while (line != null) {
            try {
                line = this.in.readLine();
                System.out.println(line);
                String anfrage = getAnfrage(line);
                System.out.println(anfrage);
                anfrageVerarbeiten(anfrage);
                while (!line.isEmpty()) {
                    this.in.readLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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
    private void anfrageVerarbeiten(String anfrage) {
        switch (anfrage) {
            case "/test" -> System.out.println("Test erfolgreich!");
        }
    }
}
