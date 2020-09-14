package rpgram.maps;

import com.crown.common.utils.Random;
import com.crown.i18n.I18n;
import com.crown.i18n.ITemplate;
import com.crown.maps.Map;
import com.crown.maps.Point3D;
import rpgram.creatures.Hare;
import rpgram.mapobjects.*;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

public class GlobalMap extends Map {
    public GlobalMap(String name, int xSize, int ySize, int zSize) {
        super(name, xSize, ySize, zSize);
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                new Grass(this, new Point3D(x, y, MapLevel.ground));
            }
        }
        for (int i = 0; i < xSize / 2; i++) {
            new Tree(this);
        }
        for (int i = 0; i < xSize / 10; i++) {
            new House(this);
        }
        for (int i = 0; i < xSize / 15; i++) {
            new Village(this);
    }

    public void initializeNpcs() {
        new Hare(this, Random.getFreePoint(this).withZ(MapLevel.ground + 1));
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
