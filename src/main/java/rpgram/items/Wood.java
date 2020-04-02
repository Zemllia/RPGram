package rpgram.items;

public class Wood extends InventoryItem {
    public Wood(int count) {
        super(0, "Дерево", ItemType.MATERIAL, count);
    }
}
