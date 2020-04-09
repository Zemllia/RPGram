package rpgram.items;

import rpgram.core.I18N;

public class RoastedMeat extends Food {
    public RoastedMeat(int count) {
        super(
            I18N.get("object.nominative.roastedMeat"),
            ItemType.MEAL,
            count,
            500
        );
    }
}
