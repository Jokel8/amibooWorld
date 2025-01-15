package verwaltung;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class Map {
    public void hochladen(String datei) {
        try {
            BufferedImage map = ImageIO.read(new File(datei));
            int[][] mapArray = new int[1000][1000];
            for (int y = 0; y < mapArray.length; y++) {
                for (int x = 0; x < mapArray[y].length; x++) {
                    mapArray[y][x] = map.getRGB(x, y);
                }
            }
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://v073086.kasserver.com:3306/d0421573";

            //Connection conn = DriverManager.getConnection(url,user,password);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
