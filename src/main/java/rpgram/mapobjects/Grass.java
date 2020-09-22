package rpgram.mapobjects;

import com.crown.common.interfaces.IDroppable;
import com.crown.items.InventoryItem;
import com.crown.maps.*;
import rpgram.items.Dirt;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class Grass extends MapObject implements IDroppable {
    public Grass(Map map, Point3D pt) {
        super(
            "Grass",
            map,
            MapIcons.getIcons().get(IconType.grass),
            pt
        );
        setWalkable(false);
    }

    @Override
    public MapIcon<?> getMapIcon() {
        return MapIcons.getIcons().get(getMapIconId());
    }

    @Override
    public InventoryItem[] drop() {
        return new InventoryItem[] { new Dirt() };
    }
}
