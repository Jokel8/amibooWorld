package server;

import economy.Category;
import economy.Inventory;
import economy.Item;
import economy.Rarity;
import org.json.JSONArray;
import org.json.JSONObject;

public class  Spieler {
    private Queue queue;
    public Inventory inventory;
    private String token;
    private long lezterZugriff;

    public Spieler(Queue queue, Inventory inventory, String token) {
        this.queue = queue;
        this.inventory = inventory;
        this.token = token;
        this.lezterZugriff = System.currentTimeMillis() / 1000L;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }
    public Queue getQueue() {
        return this.queue;
    }
    //    /**
//     * liest das inventar aus der datenbankn ein und wandelt es in ein Inventar Objekt um
//     * @param inventar Inventar als Json
//     * @return Inventar mit den Items aus der Datenbank
//     */
//    private Inventory inventarEinlesen(JSONObject inventar) {
//        if (!inventar.has("fehler")) {
//            JSONArray items = inventar.getJSONArray("items");
//            for (int i = 0; i < items.length(); i++) {
//                JSONObject item = items.getJSONObject(i);
//                inventory.addItem(new Item(item.getString("name"), item.getBoolean("stackable"), item.getInt("value"), item.getEnum(Rarity.class, "rarity"), item.getString("description"), item.getString("manufacturer"), item.getEnum(Category.class, "category"), item.getInt("item_id")));
//                inventory.addGold(inventar.getInt("toal_gold"));
//            }
//        } else {
//            Inventory fehler = new Inventory("fehler");
//            fehler.addItem(new Item("fehler", false, 0, Rarity.UNCOMMON, "falscher token", "server", Category.OTHER, 3));
//            return fehler;
//        }
//        return inventory;
//    }

    public String getToken() {
        return token;
    }
}
