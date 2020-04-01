package rpgram.items;

public class Food extends InventoryItem {
    protected int nutritionalValue;

    public Food(int id, ItemType type, int count) {
        super(id, type, count);
        this.nutritionalValue = 800;
    }

    public Food(int id, ItemType type, int count, int nutritionalValue) {
        super(id, type, count);
        this.nutritionalValue = nutritionalValue;
    }

    public Food(int id, String name, ItemType type, int count, int nutritionalValue) {
        super(id, name, type, count);
        this.nutritionalValue = nutritionalValue;
    }

    public int getNutritionalValue() {
        return nutritionalValue;
    }
}
