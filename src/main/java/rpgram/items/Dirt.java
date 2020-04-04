package rpgram.items;

import rpgram.core.I18N;

public class Dirt extends InventoryItem {
    public Dirt(int count) {
        super(1, I18N.get("object.nominative.dirt"), ItemType.MATERIAL, count);
    }
}
