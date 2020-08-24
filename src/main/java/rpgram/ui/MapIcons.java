package rpgram.ui;

import com.crown.common.ObjectsMap;
import rpgram.maps.TextMapIcon;

public class MapIcons extends ObjectsMap<TextMapIcon> {
    private static MapIcons instance;

    public static MapIcons getIcons() {
        if (instance == null) {
            synchronized (MapIcons.class) {
                if (instance == null) {
                    instance = new MapIcons();
                }
            }
        }
        return instance;
    }

    MapIcons() {
        for (IconType iconType : IconType.values()) {
            var repr = iconType.getRepresentation();
            add(new TextMapIcon("icon_" + repr.hashCode(), repr));
        }
    }

    public TextMapIcon addIcon(String name, String representation) {
        var icon = new TextMapIcon(name, representation);
        getIcons().add(icon);
        return icon;
    }

    public TextMapIcon get(IconType iconType) {
        return get("icon_" + iconType.getRepresentation().hashCode());
    }
}
