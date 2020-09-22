package rpgram.mapobjects;

import com.crown.common.interfaces.IDroppable;
import com.crown.common.utils.Random;
import com.crown.items.InventoryItem;
import com.crown.maps.*;
import rpgram.maps.MapLevel;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class House extends MapObject implements IDroppable {
    public static final int size = 3;

    public House(Map map) {
        super(
            "House",
            map,
            MapIcons.getIcons().get(IconType.house),
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
        return new InventoryItem[0];
    }
}
