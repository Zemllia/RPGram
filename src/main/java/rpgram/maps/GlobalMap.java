package rpgram.maps;

import com.crown.i18n.I18n;
import com.crown.i18n.ITemplate;
import com.crown.maps.Map;
import com.crown.maps.Point3D;
import rpgram.mapobjects.*;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class GlobalMap extends Map {
    public GlobalMap(String name, int xSize, int ySize, int zSize) {
        super(name, xSize, ySize, zSize);
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < ySize; x++) {
                add(new Grass(this, new Point3D(x, y, MapLevel.ground)));
            }
        }
        for (int i = 0; i < xSize / 2; i++) {
            add(new Tree(this));
        }
        for (int i = 0; i < xSize / 10; i++) {
            add(new House(this));
        }
        for (int i = 0; i < xSize / 15; i++) {
            add(new Village(this));
        }
    }

    @Override
    public TextMapIcon getEmptyIcon() {
        return MapIcons.getIcons().get(IconType.emptiness);
    }

    @Override
    public ITemplate getName() {
        return I18n.of("map.global.name");
    }

    @Override
    public ITemplate getDescription() {
        return I18n.empty;
    }
}
