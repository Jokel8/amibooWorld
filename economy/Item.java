package economy;

import org.json.JSONObject;

/**
 * Represents a general item in the game.
 * Items can have properties such as name, stackability, value, rarity, description, manufacturer, and category.
 */
public class Item {

    protected String name;
    protected boolean stackable;
    protected int item_id;
    protected Rarity rarity;
    protected String description;
    protected String manufacturer;
    protected Category category;
    protected int menge;

    /**
     * Default constructor initializing the item with default values.
     */
    public Item() {
        this.name = null;
        this.rarity = Rarity.COMMON;
        this.description = "description";
    }
    public JSONObject toJSON() {
        JSONObject item = new JSONObject();
        item.put("name", this.name);
        item.put("description", this.description);
        item.put("manufacturer", this.manufacturer);
        item.put("category", this.category);
        item.put("stackable", this.stackable);
        item.put("rarity", this.rarity);
        item.put("item_id", this.item_id);
        item.put("menge", this.menge);
        return item;
    }

    /**
     * create an item.
     *
     * @param name        The name of the item.
     * @param stackable   Whether the item is stackable.
     * @param rarity      The rarity of the item.
     * @param description A description of the item.
     * @param manufacturer The manufacturer of the item.
     * @param category    The category of the item.
     * @param item_id item id aus der DATENBANK entnehmen
     * @return A new instance of an Item (usually a subclass like Weapon).
     */
    public Item(String name, boolean stackable, Rarity rarity, String description, String manufacturer, Category category, int item_id, int menge) {
        this.name = name;
        this.stackable = stackable;
        this.rarity = rarity;
        this.description = description;
        this.manufacturer = manufacturer;
        this.category = category;
        this.item_id = item_id;
        this.menge = menge;
    }



    // Getters and Setters
    public String getName() {
        return name;
    }

    public boolean isStackable() {
        return stackable;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public String getDescription() {
        return description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Category getCategory() {
        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    //public Weapon createItem(String name, boolean stackable, int value, Rarity rarity, String description, String manufacturer, Category category, int damage);
}
