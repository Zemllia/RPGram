package rpgram.items;

public class Money extends InventoryItem {
    public Money(int count) {
        super(-1001, "Coins", ItemType.MONEY, count);
    }
}
