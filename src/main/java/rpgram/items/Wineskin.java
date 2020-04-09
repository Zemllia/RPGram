package rpgram.items;

import rpgram.core.I18N;

public class Wineskin extends InventoryItem {
    public Wineskin(int count) {
        super(
            I18N.get("object.nominative.wineskin"),
            ItemType.DRINK,
            count
        );
    }
}
