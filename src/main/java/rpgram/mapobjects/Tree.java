package rpgram.mapobjects;

import com.crown.common.interfaces.IDroppable;
import com.crown.common.utils.Random;
import com.crown.items.InventoryItem;
import com.crown.maps.*;
import rpgram.items.Wood;
import rpgram.maps.MapLevel;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class Tree extends MapObject implements IDroppable {
    public static final int size = 3;

    public Tree(Map map) {
        super(
            "Tree",
            map,
            MapIcons.getIcons().get(IconType.tree),
            LargeObjectTemplates.getSquareLinearZTemplate(
                Random.getFreePoint(map, size, size).withZ(MapLevel.ground + 1),
                size
            )
        );
        setWalkable(false);
    }

    @Override
    public MapIcon<?> getMapIcon() {
        return MapIcons.getIcons().get(getMapIconId());
    }

    @Override
    public InventoryItem[] drop() {
        var count = Random.getInt(1, 3);
        var items = new InventoryItem[count];
        for (int i = 0; i < count; i++) {
            items[i] = new Wood();
        }
        return items;
    }
}
