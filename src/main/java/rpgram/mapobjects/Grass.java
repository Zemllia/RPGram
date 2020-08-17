package rpgram.mapobjects;

import com.crown.maps.*;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class Grass extends MapObject {
    public Grass(Map map, Point3D pt) {
        super(
            "Grass",
            map,
            MapIcons.getIcons().get(IconType.grass),
            MapWeight.BLOCKS_LIGHT,
            pt
        );
    }

    @Override
    public MapIcon<?> getMapIcon() {
        return MapIcons.getIcons().get(getMapIconId());
    }
}
