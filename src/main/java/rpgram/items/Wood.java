package rpgram.items;

import rpgram.core.I18N;

public class Wood extends InventoryItem {
    public Wood(int count) {
        super(0, I18N.get("object.nominative.wood"), ItemType.MATERIAL, count);
    }
}
