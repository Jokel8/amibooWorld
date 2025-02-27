package economy;

public class Gestein extends Item{
    public Gestein(int menge){
        super("gestein", true, Rarity.COMMON, "Stein", "Natur", Category.MATERIAL, 3, menge);
    }
}
