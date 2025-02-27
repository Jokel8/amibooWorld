package economy;

public class Holz extends Item {
    public Holz(int menge){
        super("holz", true, Rarity.COMMON, "Holz", "Natur", Category.MATERIAL, 2, menge);
    }
}
