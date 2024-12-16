package spieler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private void anfrageVerarbeiten(String anfrage) throws IOException {
        switch (anfrage) {
            case "/test" -> {
                System.out.println("Test erfolgreich!");
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                String antwort = "HTTP/1.1 200 OK\n" +
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
                        "Age: 7\n" +
                        "\n" +
                        "<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "  <meta charset=\"utf-8\">\n" +
                        "  <title>A simple webpage</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <h1>Simple HTML webpage</h1>\n" +
                        "  <p>Hello, world!</p>\n" +
                        "</body>\n" +
                        "</html>";
                out.println(antwort);
            }
        }
    }
}
