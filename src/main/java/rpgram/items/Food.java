package rpgram.items;

public class Food extends InventoryItem {
    protected final int nutritionalValue;

    public Food(int id, ItemType type, int count) {
        super(type, count);
        this.nutritionalValue = 800;
    }

    public Food(int id, ItemType type, int count, int nutritionalValue) {
        super(type, count);
        this.nutritionalValue = nutritionalValue;
    }

    public Food(int id, String name, ItemType type, int count, int nutritionalValue) {
        super(name, type, count);
        this.nutritionalValue = nutritionalValue;
    }

    public int getNutritionalValue() {
        return nutritionalValue;
    }
}
