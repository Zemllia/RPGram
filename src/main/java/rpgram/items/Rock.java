package rpgram.items;

public class Rock extends InventoryItem {
    public Rock(int count) {
        super(2, "Камень", ItemType.MATERIAL, count);
    }
}
