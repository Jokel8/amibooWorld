package economy;

public class DumbItem extends Item{
    public DumbItem(String name, int menge, int id) {
        super(name, false, Rarity.COMMON, "", "", Category.OTHER, id, menge);
    }
}
