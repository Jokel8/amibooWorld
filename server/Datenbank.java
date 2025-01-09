package server;

public class Datenbank {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    private String driver;
    private String url;
    private Json
    public Datenbank() {
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://localhost:3306/d0421573";
    }
    private String[][] getMap(int x, int y, int radius) {
        int[][] tiles = welcheTileSollIchHolen(x, y, radius);

    }
    private int[][] welcheTileSollIchHolen(int x, int y, int radius) {
        int[][] tiles = new int[radius+radius*radius+radius][2];
        int i = 0;
        for (int x_player = x - radius; x_player <= x + radius; x_player++ ) {
            for (int y_player = y - radius; y_player <= y + radius; y_player++ ) {
                tiles[i] = new int[]{x_player, y_player};
                i++;
            }
        }
        return tiles;
    }

    /**
     * Prozedur, um das Programm mit der Datenbank zu verknüpfen.
     *
     */
    private void dbConnect (){
        try {
            Class.forName(driver);

            Connection con = DriverManager.getConnection("jdbc:mysql://v073086.kasserver.com/d0421573","d0421573", "pZuw7TVdwLCqWUjMUD8o");
        } catch (SQLException | ClassNotFoundException e) {

        }
    }

    private int[][] dbGetTile (int x, int y) throws SQLException {
        String sql = "select * from tiles where x=" + x + " and y=" + y;
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();
        return;


    }

}
