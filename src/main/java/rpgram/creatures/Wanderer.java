package rpgram.creatures;

import com.crown.maps.*;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class Wanderer extends Human {
    public Wanderer(long telegramId, String name, Map map, Point3D pt) {
        super(
            telegramId,
            name,
            map,
            MapIcons.getIcons().get(IconType.defaultPlayer),
            pt
        );
        setWalkable(false);
    }
}
