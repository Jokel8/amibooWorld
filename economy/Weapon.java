package economy;

import org.json.JSONObject;

/**
 * Represents a weapon item in the game, inheriting from the general Item class.
 * A weapon has a damage value in addition to the usual properties of an item.
 */
public class Weapon extends Item {

    private int damage; // The damage value for the weapon

    /**
     * Getter for the damage value of the weapon.
     *
     * @return The damage value of the weapon.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Setter for the damage value of the weapon.
     *
     * @param damage The damage value to set for the weapon.
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }


    /**
     * Factory method to create a new Weapon instance with specific attributes.
     *
     * @param name        The name of the weapon.
     * @param stackable   Whether the weapon is stackable.
     * @param value       The value of the weapon in gold.
     * @param rarity      The rarity of the weapon.
     * @param description A description of the weapon.
     * @param manufacturer The manufacturer of the weapon.
     * @param category    The category of the weapon.
     * @param damage      The damage value of the weapon.
     * @return A newly created Weapon instance.
     */
    public Weapon createItem(String name, boolean stackable, int value, Rarity rarity, String description, String manufacturer, Category category, int damage) {
        Weapon weapon = new Weapon();
        weapon.setName(name);
        weapon.setStackable(stackable);
        weapon.setValue(value);
        weapon.setRarity(rarity);
        weapon.setDescription(description);
        weapon.setManufacturer(manufacturer);
        weapon.setCategory(category);
        weapon.setDamage(damage);
        weapon.setItem_id(6);
        return weapon;
    }
    @Override
    public JSONObject toJSON() {
        JSONObject weapon = new JSONObject();
        weapon.put("name", this.getName());
        weapon.put("stackable", this.isStackable());
        weapon.put("value", this.getValue());
        weapon.put("rarity", this.getRarity());
        weapon.put("description", this.getDescription());
        weapon.put("manufacturer", this.getManufacturer());
        weapon.put("category", this.getCategory());
        weapon.put("damage", this.getDamage());
        weapon.put("item_id", this.getItem_id());
        return weapon;
    }
}
