package rpgram.items;

import rpgram.core.NamedObject;
import rpgram.core.utils.Random;

public class InventoryItem extends NamedObject {
    int count;
    String icon = "?";
    String description = "Разработчик забыл добавить описание предмету...";
    ItemType type;

    public InventoryItem(int id, ItemType type, int count) {
        super(-Random.randInt(1000, 1000000), "");
        this.name = this.getClass().getSimpleName();
        this.count = count;
        this.type = type;
    }

    public InventoryItem(int id, String name, ItemType type, int count) {
        super(-Random.randInt(1000, 1000000), name);
        this.count = count;
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public ItemType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void increaseCount(int delta) {
        count += delta;
    }
}
