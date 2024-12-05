import java.net.*;
import java.io.*;

    public class Server {

        final int PORT = 9999;
        final int MAX = 200;
        public Server() throws IOException {
            int countConnections = 0;
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Lausche an Port " + PORT);
            while(true) {
                Socket client = server.accept();
                System.out.println("Browser verbunden: " + client.getInetAddress().getHostName());
                countConnections++;
                if (countConnections <= MAX) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                PrintWriter out = new PrintWriter(client.getOutputStream(),true);
                                String msg;
                                String firstLine = "";
                                boolean isImage = false;
                                while ((msg = in.readLine()) != null) {
                                    if (firstLine.isEmpty()) {
                                        firstLine = msg;
                                        firstLine = firstLine.substring(5,firstLine.length()-9);
                                        System.out.println("FIRSTLINE: " + firstLine);
                                        if (firstLine.contains(".jpg") || firstLine.contains(".png")) {
                                            isImage = true;
                                        }
                                    }
                                    System.out.println(msg);
                                    if (msg.isEmpty()) {
                                        System.out.println("Leere Nachricht");
                                        break;
                                    }

                                }


                                out.println("HTTP/1.1 200 OK");
                                out.println("Content-Type: text/html");
                                out.println();
                                File f = new File(firstLine);

                                if (!isImage) {
                                    FileReader fr = new FileReader(firstLine);
                                    BufferedReader brfr = new BufferedReader(fr);
                                    String line;
                                    while ((line = brfr.readLine()) != null) {
                                        out.println(line);
                                    }
                                    brfr.close();
                                } else {
                                    FileInputStream fis = new FileInputStream(firstLine);
                                    byte[] buffer = new byte[1024];
                                    int bytesRead;

                                    while ((bytesRead = fis.read(buffer)) != -1) {
                                        client.getOutputStream().write(buffer,0,bytesRead);
                                    }
                                    fis.close();
                                }

                                out.flush();
                                in.close();
                                out.close();
                                client.close();
                                System.out.println("Verbindung abgeschlossen.");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                } else {
                    break;
                }


            }


        }

}
