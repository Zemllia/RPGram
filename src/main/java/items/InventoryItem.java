package items;

public class InventoryItem {
    protected String icon = "NaN";
    protected int itemID = 0;
    protected String name = "NaN";
    protected int count = 0;
    protected String type = "building_material";

    public String getName(){
        return name;
    }

    public int getCount(){
        return count;
    }

    public void increaseCount(int delta){
        count += delta;
    }
}
