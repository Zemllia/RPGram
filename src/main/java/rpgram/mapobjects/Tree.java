package rpgram.mapobjects;

import com.crown.common.utils.Random;
import com.crown.maps.*;
import rpgram.maps.MapLevel;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class Tree extends MapObject {
    public static final int size = 3;

    public Tree(Map map) {
        super(
            "Tree",
            map,
            MapIcons.getIcons().get(IconType.tree),
            MapWeight.OBSTACLE,
            LargeObjectTemplates.getSquareLinearZTemplate(
                Random.getFreePoint(map, size, size).withZ(MapLevel.ground + 1),
                size
            )
        );
    }

    @Override
    public MapIcon<?> getMapIcon() {
        return MapIcons.getIcons().get(getMapIconId());
    }
}
