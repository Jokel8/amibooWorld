import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class ImagetoMap {

    public static String[][] hexArray = new String[999][999];
    public static void getImage(String pfad) throws IOException {
        File inputFile = new File(pfad);
        BufferedImage image = ImageIO.read(inputFile);

        int width = image.getWidth();
        int height = image.getHeight();

        if(width != 1000 || height != 1000){
            throw new IllegalArgumentException("Das Bild entspricht nicht der benötigten größe von 1000x1000 Pixeln");
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // RGB-Wert auslesen
                int rgb = image.getRGB(x, y);

                // In Hexadezimal umwandeln
                String hex = String.format("#%06X", (rgb & 0xFFFFFF));

                // Wert ins Array speichern
                hexArray[x][y] = hex;
            }
        }

    }

    public static void loadImage() throws ClassNotFoundException, SQLException {
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        String url = "jdbc:mysql://v073086.kasserver.com/d0421573/map";
        Connection Conn = DriverManager.getConnection(url,"d0421573","pZuw7TVdwLCqWUjMUD8o");
        String feldtyp = "";
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                String hexwert = hexArray[x][y];
                if(hexwert == "PLATZHALTER"){ //TODO Wichtig weil sonst nix gehen
                    feldtyp = "Wüste"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Wasser"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Gras"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Berg"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Vulkan"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Weg"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Schnee"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Blumenfeld"; //TODO Wichtig weil wieder sonst nix gehen
                }
                else if(hexwert == "PLATZHALTER"){
                    feldtyp = "Eis"; //TODO Wichtig weil wieder sonst nix gehen
                }
                String insertSQL = "INSERT INTO map(field_x, field_y, field_type) VALUES(" + x + ", " + y +", " + feldtyp +");";
                PreparedStatement statement = Conn.prepareStatement(insertSQL);
            }
        }
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        System.out.println("Bitte gib den Dateipfad der Datei an die du hochladen möchtest");
        Scanner scanner = new Scanner(System.in);
        String eingabe = scanner.nextLine();

        getImage(eingabe);
        loadImage();
    }
}