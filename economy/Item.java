package economy;

/**
 * Represents a general item in the game.
 * Items can have properties such as name, stackability, value, rarity, description, manufacturer, and category.
 */
public abstract class Item {

    protected String name;
    protected boolean stackable;
    protected int value;
    protected Rarity rarity;
    protected String description;
    protected String manufacturer;
    protected Category category;

    /**
     * Default constructor initializing the item with default values.
     */
    public Item() {
        this.value = 0;
        this.name = null;
        this.rarity = Rarity.COMMON;
        this.description = "description";
    }

    /**
     * Factory method to create an item.
     *
     * @param name        The name of the item.
     * @param stackable   Whether the item is stackable.
     * @param value       The value of the item.
     * @param rarity      The rarity of the item.
     * @param description A description of the item.
     * @param manufacturer The manufacturer of the item.
     * @param category    The category of the item.
     * @return A new instance of an Item (usually a subclass like Weapon).
     */
    public abstract Item createItem(String name, boolean stackable, int value, Rarity rarity, String description, String manufacturer, Category category);

    // Getters and Setters
    public String getName() {
        return name;
    }

    public boolean isStackable() {
        return stackable;
    }

    public int getValue() {
        return value;
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

    public void setValue(int value) {
        this.value = value;
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

    public abstract Weapon createItem(String name, boolean stackable, int value, Rarity rarity, String description, String manufacturer, Category category, int damage);
}
