package rpgram.mapobjects;

import com.crown.common.utils.Random;
import com.crown.maps.*;
import rpgram.maps.MapLevel;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class Village extends MapObject {
    public static final int size = 3;

    public Village(Map map) {
        super(
            "Village",
            map,
            MapIcons.getIcons().get(IconType.village),
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
