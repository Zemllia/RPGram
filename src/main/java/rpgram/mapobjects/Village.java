package rpgram.mapobjects;

import com.crown.common.interfaces.IDroppable;
import com.crown.common.utils.Random;
import com.crown.items.InventoryItem;
import com.crown.maps.*;
import rpgram.maps.MapLevel;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class Village extends MapObject implements IDroppable {
    public static final int size = 3;

    public Village(Map map) {
        super(
            "Village",
            map,
            MapIcons.getIcons().get(IconType.village),
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
