package economy.resourcen;

import economy.Category;
import economy.Item;
import economy.Rarity;

public class Stein extends Item {
    public Stein(int menge){
        super("stein", true, Rarity.COMMON, "Stein", "Natur", Category.MATERIAL, 3, menge);
    }
}
