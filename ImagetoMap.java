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

        System.out.println(width);
        System.out.println(height);

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
                if(hexwert.equals("#DDD36E")){
                    feldtyp = 1;
                    //wuest
                }
                else if(hexwert.equals("#0096DC")){
                    feldtyp = 2;
                    //wasser
                }
                else if(hexwert.equals("#93C751")){
                    feldtyp = 3;
                    //gras
                }
                else if(hexwert.equals("#6B6B6B")){
                    feldtyp = 14;
                    //berg
                }
                else if(hexwert.equals("#070505")){
                    feldtyp = 6;
                    //vulkan
                }
                else if(hexwert.equals("#339E45")){
                    feldtyp = 10;
                    //blumenfeld
                }
                else if(hexwert.equals("#50392A")){
                    feldtyp = 8;
                    //pampa
                }
                else if(hexwert.equals("#423C56")){
                    feldtyp = 16;
                    //lava
                }
                else if(hexwert.equals("#BBF7FF")){
                    feldtyp = 9;
                    //schnee
                }
                else if(hexwert.equals("#15E3FF")){
                    feldtyp = 9;
                    //eis
                }
                else if(hexwert.equals("#0C4E25")) {
                    feldtyp = 4;
                    //jungle
                }
                else {
                    System.out.println("fehler x: " + x+ " y: " + y);
                }
                if(x == 0 && y == 0){
                    insertSQL = "\nINSERT INTO map(field_x, field_y, field_type) \nVALUES";
                }
                else if (x == 999 && y == 999){
                    insertSQL = "\n(" + x + ", " + y +", " + feldtyp +");";
                }
                else{
                    insertSQL = "\n(" + x + ", " + y +", " + feldtyp +"),";
                }
                fileWriter.write(insertSQL);
                //System.out.println(x);
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
