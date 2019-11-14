package items;

public class Wineskin extends InventoryItem{
    public Wineskin (int count) {
        this.count = count;
        this.name = "Бурдюк воды";
        this.icon = " ";
        this.itemID = 4;
        this.type = "drinkable";
    }
}
