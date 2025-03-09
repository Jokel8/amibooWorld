package economy;

import server.Datenbank;

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
        Inventory player1Inventory = new Inventory();  // Player starts with 100 gold
        Inventory player2Inventory = new Inventory(); // Merchant starts with 500 gold

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
//
        // Add created weapons to merchant's inventory
//        player1Inventory.addItem(Holzschwert);
//        player1Inventory.addItem(Kriegsbeil);
//        player1Inventory.addItem(YamadaKatana);
        player1Inventory.addItem(new Holz(20));
//
//        // Display initial inventories before trade
//        System.out.println("Before Trade:");
//        System.out.println("Player Inventory:");
//        player1Inventory.listItems();
//        System.out.println("Merchant Inventory:");
//        player2Inventory.listItems();
//
//        // Player attempts to buy the Yamada Katana from the merchant
//        boolean tradeSuccess = player1Inventory.tradeGoldForItem(YamadaKatana, 500, player2Inventory);
//        if (tradeSuccess) {
//            System.out.println("\nTrade Successful! Player bought Yamada Taketsune's Katana.");
//        } else {
//            System.out.println("\nTrade Failed! Not enough gold.");
//        }
//
//        // Display inventories after the trade
//        System.out.println("\nAfter Trade:");
//        System.out.println("Player Inventory:");
//        player1Inventory.listItems();
//        System.out.println("Merchant Inventory:");
//        player2Inventory.listItems();


        //System.out.println(player1Inventory.listItems());

        //Datenbank test = new Datenbank();
        //test.verbinden();
        //Inventory testinventory = test.inventarEinlesen("123456");
        System.out.println(player1Inventory.listItems());
   }
}
