package economy;

/**
 * This class demonstrates a simple player-based economy where a player can purchase items (weapons)
 * from a merchant inventory using gold.
 */
public class PlayerBasedEco {

    /**
     * Main method that simulates a trade between a player and a merchant.
     * The player has a limited amount of gold and can buy weapons from the merchant.
     *
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        // Create player and merchant inventories
        Inventory playerInventory = new Inventory(100);  // Player starts with 100 gold
        Inventory merchantInventory = new Inventory(500); // Merchant starts with 500 gold

        // Create a factory instance for Weapon to generate new weapons
        Weapon weaponFactory = new Weapon();

        // Create different weapons using the factory method
        Weapon Holzschwert = weaponFactory.createItem(
                "Holzschwert",
                false,
                5,
                Rarity.COMMON,
                "Schwert aus Holz",
                "Sniperspyder2550",
                Category.WEAPON,
                2 // Damage
        );

        Weapon Kriegsbeil = weaponFactory.createItem(
                "Kriegsbeil",
                false,
                12,
                Rarity.UNCOMMON,
                "Kriegsbeil. Vor kurzem ausgegraben",
                "UNBEKANNT",
                Category.WEAPON,
                4 // Damage
        );

        Weapon YamadaKatana = weaponFactory.createItem(
                "Yamada Taketsune's Katana",
                false,
                500,
                Rarity.RELIC,
                "Geschmiedet von Meisterschmied Nomura Tanenaga für den Shogun Yamada Taketsune im Jahre 1677",
                "Nomura Tanenaga",
                Category.WEAPON,
                37 // Damage
        );

        // Add created weapons to merchant's inventory
        merchantInventory.addItem(Holzschwert);
        merchantInventory.addItem(Kriegsbeil);
        merchantInventory.addItem(YamadaKatana);

        // Display initial inventories before trade
        System.out.println("Before Trade:");
        System.out.println("Player Inventory:");
        playerInventory.listItems();
        System.out.println("Merchant Inventory:");
        merchantInventory.listItems();

        // Player attempts to buy the Yamada Katana from the merchant
        boolean tradeSuccess = playerInventory.tradeGoldForItem(YamadaKatana, 500, merchantInventory);
        if (tradeSuccess) {
            System.out.println("\nTrade Successful! Player bought Yamada Taketsune's Katana.");
        } else {
            System.out.println("\nTrade Failed! Not enough gold.");
        }

        // Display inventories after the trade
        System.out.println("\nAfter Trade:");
        System.out.println("Player Inventory:");
        playerInventory.listItems();
        System.out.println("Merchant Inventory:");
        merchantInventory.listItems();
    }
}
