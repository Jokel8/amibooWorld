package server;

import economy.Category;
import economy.Inventory;
import economy.Item;
import economy.Rarity;
import org.json.JSONArray;
import org.json.JSONObject;

public class Spieler {
    private JSONObject queue;
    private Inventory inventory;
    private int[] position;
    private long startZeit;
    private double geschwindigkeit;

    public Spieler(String queue, String inventory, int positionX, int positionY, double geschwindigkeit, long startZeit) {
        this.queue = new JSONObject(queue);
        this.inventory = this.inventarEinlesen(inventory);
        this.position = new int[]{positionX, positionY};
        this.startZeit = startZeit;
        this.geschwindigkeit = geschwindigkeit;
    }

    public int[] getPosition() {
        JSONArray queueliste = this.queue.getJSONArray("queue");
        int[] position = new int[2];
        long zeit = System.currentTimeMillis() / 1000;
        long vergangenezeit = zeit - startZeit;
        JSONObject pos = queueliste.getJSONObject((int)(vergangenezeit / geschwindigkeit));
        position[0] = pos.getInt("x");
        position[1] = pos.getInt("y");
        return position;
    }

    public Inventory getInventory() {
        return inventory;
    }

    /**
     * liest das inventar aus der datenbankn ein und wandelt es in ein Inventar Objekt um
     * @param inventar Inventar als Json
     * @return Inventar mit den Items aus der Datenbank
     */
    private Inventory inventarEinlesen(String inventar) {
        JSONObject json = new JSONObject(inventar);
        if (!json.has("fehler")) {
            JSONArray items = json.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                inventory.addItem(new Item(item.getString("name"), item.getBoolean("stackable"), item.getInt("value"), item.getEnum(Rarity.class, "rarity"), item.getString("description"), item.getString("manufacturer"), item.getEnum(Category.class, "category"), item.getInt("item_id")));
                inventory.addGold(json.getInt("toal_gold"));
            }
        } else {
            Inventory fehler = new Inventory("fehler");
            fehler.addItem(new Item("fehler", false, 0, Rarity.UNCOMMON, "falscher token", "server", Category.OTHER, 3));
            return fehler;
        }
        return inventory;
    }
}
