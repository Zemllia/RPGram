package rpgram;

import rpgram.core.Position;
import rpgram.items.InventoryItem;

import java.util.ArrayList;

public class Treasure {
    private final ArrayList<InventoryItem> inventoryItem = new ArrayList<>();
    private final Position position;
    private final String nameOfPlayer;

    public Treasure(InventoryItem inventoryItem, Position position, String nameOfPlayer) {
        this.inventoryItem.add(inventoryItem);
        this.nameOfPlayer = nameOfPlayer;
        this.position = position;
    }

    public ArrayList<InventoryItem> getInventoryItem() {
        return inventoryItem;
    }

    public Position getTreasurePosition() {
        return position;
    }

    public String getNameOfPlayer() {
        return nameOfPlayer;
    }

    public void addNewItem(InventoryItem newItem) {
        inventoryItem.add(newItem);
    }
}
