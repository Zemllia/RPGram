package rpgram.items;

import rpgram.core.NamedObject;

public class InventoryItem extends NamedObject {
    int count;
    String icon = "?";
    final String description = "Разработчик забыл добавить описание предмету...";
    final ItemType type;

    public InventoryItem(ItemType type, int count) {
        super("");
        this.name = this.getClass().getSimpleName();
        this.count = count;
        this.type = type;
    }

    public InventoryItem(String name, ItemType type, int count) {
        super(name);
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
