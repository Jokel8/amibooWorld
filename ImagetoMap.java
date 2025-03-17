import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class ImagetoMap {

    public static String[][] hexArray = new String[1000][1000];
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

    public static void loadImage() throws ClassNotFoundException, SQLException, IOException {
        File file = new File("C:/Users/fynnh/OneDrive/Dokumente/SQL_Abfragen/Abfrage1.sql");
        FileWriter fileWriter = new FileWriter(file);
        String insertSQL = "";
        int feldtyp = 0;
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                String hexwert = hexArray[x][y];
                //System.out.println(hexwert);
                if(hexwert.equals("#ddd36e")){
                    feldtyp = 1;
                    //wuest
                }
                else if(hexwert.equals("#0096dc")){
                    feldtyp = 2;
                    //wasser
                }
                else if(hexwert.equals("#93c751")){
                    feldtyp = 3;
                    //gras
                }
                else if(hexwert.equals("#6b6b6b")){
                    feldtyp = 14;
                    //berg
                }
                else if(hexwert.equals("#070505")){
                    feldtyp = 6;
                    //vulkan
                }
                else if(hexwert.equals("#339e45")){
                    feldtyp = 10;
                    //blumenfeld
                }
                else if(hexwert.equals("#50392a")){
                    feldtyp = 8;
                    //pampa
                }
                else if(hexwert.equals("#423c56")){
                    feldtyp = 16;
                    //lava
                }
                else if(hexwert.equals("#bbf7ff")){
                    feldtyp = 9;
                    //schnee
                }
                else if(hexwert.equals("#15e3ff")){
                    feldtyp = 9;
                    //eis
                }
                else if(hexwert.equals("#0c4e25")) {
                    feldtyp = 4;
                    //jungle
                }

                insertSQL = "\nINSERT INTO map(field_x, field_y, field_type) VALUES(" + x + ", " + y +", " + feldtyp +");";
                fileWriter.write(insertSQL);
                System.out.println(x);
                //System.out.println(insertSQL);
            }
        }
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        System.out.println("Bitte gib den Dateipfad der Datei an die du hochladen möchtest");
        Scanner scanner = new Scanner(System.in);
        String eingabe = scanner.nextLine();

        getImage(eingabe);
        loadImage();
        System.out.println("Finished");
    }
}
