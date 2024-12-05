import java.net.*;
import java.io.*;

public class Server {

    final int PORT = 9999;  // The port the server will listen on
    final int MAX = 200;    // Maximum number of allowed connections
    public Server() throws IOException {
        int countConnections = 0;  // To count the number of connections
        ServerSocket server = new ServerSocket(PORT);  // Creating a server socket on the specified port

        System.out.println("Server listening on port " + PORT);  // Server is now active

        while(true) {
            // Accept a client connection
            Socket client = server.accept();
            System.out.println("Browser connected: " + client.getInetAddress().getHostName());  // Client IP

            countConnections++;

            // If the connection count is within the allowed limit
            if (countConnections <= MAX) {
                new Thread(() -> {
                    try {
                        // Input and output streams for reading from and writing to the client
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                        String msg;
                        String firstLine = "";  // To store the requested file path
                        boolean isImage = false; // Flag to check if the requested file is an image

                        // Read the incoming HTTP request line by line
                        while ((msg = in.readLine()) != null) {
                            if (firstLine.isEmpty()) {
                                // Extract the requested file path (like /index.html)
                                firstLine = msg;
                                firstLine = firstLine.substring(5, firstLine.length() - 9);  // Removing 'GET' and 'HTTP/1.1'
                                System.out.println("Requested file: " + firstLine);

                                // Check if the requested file is an image
                                if (firstLine.contains(".jpg") || firstLine.contains(".png")) {
                                    isImage = true;
                                }
                            }
                            System.out.println(msg);  // Print the whole HTTP request (for debugging)
                            if (msg.isEmpty()) {
                                break;  // End of request headers (after the empty line)
                            }
                        }

                        // Send HTTP response headers to the client
                        out.println("HTTP/1.1 200 OK");
                        out.println("Content-Type: text/html");
                        out.println();  // Blank line separates headers from body

                        // Determine the requested file path
                        File f = new File(firstLine);
                        if (!f.exists()) {
                            // If the file doesn't exist, send a 404 error response
                            out.println("<h1>404 Not Found</h1>");
                        } else {
                            // If the file exists, send its contents to the client
                            if (!isImage) {
                                // For non-image files (like HTML or text files)
                                FileReader fr = new FileReader(firstLine);
                                BufferedReader buffer = new BufferedReader(fr);
                                String line;
                                while ((line = buffer.readLine()) != null) {
                                    out.println(line);  // Print each line of the file
                                }
                                buffer.close();
                            } else {
                                // For image files (like .jpg or .png)
                                FileInputStream fis = new FileInputStream(firstLine);
                                byte[] buffer = new byte[1024];
                                int bytesRead;

                                while ((bytesRead = fis.read(buffer)) != -1) {
                                    client.getOutputStream().write(buffer, 0, bytesRead);  // Send file in chunks
                                }
                                fis.close();
                            }
                        }

                        // Close streams and client connection
                        out.flush();
                        in.close();
                        out.close();
                        client.close();
                        System.out.println("Connection closed with client.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();  // Handle the client request in a separate thread
            } else {
                break;  // Stop accepting connections if the max is reached
            }
        }
    }

}
