package economy.resourcen;

import economy.Category;
import economy.Item;
import economy.Rarity;

public class Gold extends Item {
    public Gold(int menge){
        super("gold", true, Rarity.UNCOMMON, "Gold", "Natur", Category.MATERIAL, 1, menge);
    }
}
