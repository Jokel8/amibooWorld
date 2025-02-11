package economy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import server.HttpServer.*;

/**
 * Represents an inventory that can store items and manage gold for a player or merchant.
 * The inventory allows adding/removing items and managing the amount of gold the owner has.
 */
public class Inventory {

    private List<Item> items; // Storage for items in the inventory
    private int gold;         // Storage for gold in the inventory
    public String username;

    /**
     * Constructor to initialize the inventory with a starting amount of gold.
     *
     */
    public Inventory(String username) {
        this.items = new ArrayList<>();
        this.username = username;
    }

    /**
     * Adds an item to the inventory.
     *
     * @param item The item to be added to the inventory.
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Removes an item from the inventory.
     *
     * @param item The item to be removed from the inventory.
     * @return true if the item was removed successfully, false otherwise.
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    /**
     * Gets the current amount of gold in the inventory.
     *
     * @return The current gold in the inventory.
     */
    public int getGold() {
        return gold;
    }

    /**
     * Adds a certain amount of gold to the inventory.
     *
     * @param amount The amount of gold to be added.
     */
    public void addGold(int amount) {
        this.gold += amount;
    }

    /**
     * Removes a certain amount of gold from the inventory.
     *
     * @param amount The amount of gold to be removed.
     * @return true if the removal was successful, false if there is not enough gold.
     */
    public boolean removeGold(int amount) {
        if (gold >= amount) {
            this.gold -= amount;
            return true;
        }
        return false; // Not enough gold
    }

    /**
     * Trades an item for gold. The item is removed from the inventory, and the gold is transferred to the other inventory.
     *
     * @param item        The item being traded.
     * @param price       The price of the item in gold.
     * @param otherInventory The other inventory to which the item will be transferred.
     * @return true if the trade was successful, false otherwise.
     */
    public boolean tradeItemForGold(Item item, int price, Inventory otherInventory) {
        if (otherInventory.removeGold(price)) {
            otherInventory.addItem(item);
            this.addGold(price);
            this.removeItem(item);
            return true;
        }
        return false; // Trade failed (other inventory doesn't have enough gold)
    }

    /**
     * Trades gold for an item. The gold is removed from the current inventory, and the item is transferred to it.
     *
     * @param item        The item to be purchased.
     * @param price       The price of the item in gold.
     * @param otherInventory The inventory from which the item will be transferred.
     * @return true if the trade was successful, false otherwise.
     */
    public boolean tradeGoldForItem(Item item, int price, Inventory otherInventory) {
        if (this.removeGold(price)) {
            this.addItem(item);
            otherInventory.addGold(price);
            otherInventory.removeItem(item);
            return true;
        }
        return false; // Trade failed (not enough gold)
    }
    public String getUsername() {
        return username;
    }
    /**
     * Displays all items in the inventory along with the total amount of gold.
     */
    public String listItems() {
        JSONObject json = new JSONObject();
        JSONArray inventar = new JSONArray();

        for (Item item : items) {
            inventar.put(item.toJSON());
        }
        json.put("items", inventar);
        json.put("toal_gold", this.gold);
        return json.toString(4);
    }

}
