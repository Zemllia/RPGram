package rpgram.items;

public class Wineskin extends InventoryItem {
    public Wineskin(int count) {
        super(4, "Бурдюк воды", ItemType.DRINK, count);
    }
}
