package rpgram.items;

import rpgram.core.I18N;

public class Coin extends InventoryItem {
    public Coin(int count) {
        super(-1001, I18N.get("object.nominative.coin"), ItemType.MONEY, count);
    }
}
