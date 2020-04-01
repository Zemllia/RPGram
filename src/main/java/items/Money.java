package items;

public class Money extends InventoryItem {
    public Money(int count) {
        this.icon = "";
        this.count = count;
        this.itemID = 1;
        this.name = "Coins";
        this.type = "money";
    }
}
