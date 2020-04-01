package rpgram.items;

public class InventoryItem {
    String icon = "NaN";
    int itemID = 0;
    protected String name = "NaN";
    int count = 0;
    String description = "Разработчик забыл добавить описание предмету...";
    String type = "building_material";

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void increaseCount(int delta) {
        count += delta;
    }
}
