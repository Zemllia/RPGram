package rpgram.items;

import rpgram.core.I18N;

public class Rock extends InventoryItem {
    public Rock(int count) {
        super(I18N.get("object.nominative.rock"), ItemType.MATERIAL, count);
    }
}
