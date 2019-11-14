package items;

public class RoastMeat extends InventoryItem{
    public RoastMeat (int count) {
        this.count = count;
        this.name = "Жаренное мясо";
        this.icon = " ";
        this.itemID = 3;
        this.type = "eatable";
    }
}
