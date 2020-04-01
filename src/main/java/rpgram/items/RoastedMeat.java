package rpgram.items;

public class RoastedMeat extends Food {
    public RoastedMeat(int count) {
        this.count = count;
        this.name = "Жаренное мясо";
        this.icon = " ";
        this.itemID = 3;
        this.type = "eatable";
        nutritionalValue = 500;
    }
}
