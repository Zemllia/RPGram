package items;

public class ShopItem {
    private InventoryItem item;
    private int count;
    private int cost;

    public ShopItem(InventoryItem item, int count, int cost){
        this.item = item;
        this.count = count;
        this.cost = cost;
    }

    public InventoryItem getItem(){
        return  item;
    }
    public int getCount(){
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    public int getCost(){
        return  cost;
    }
}
