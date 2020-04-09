package rpgram.core;

import rpgram.maps.BaseMap;

public class GameObject extends NamedObject {
    protected BaseMap map;
    private final char mapIcon;
    private final char mapWeight;

    protected Position position;
    protected Position lastPos;

    public GameObject(
        String name,
        BaseMap map,
        char mapIcon,
        char mapWeight,
        Position position
    ) {
        super(name);
        this.map = map;
        this.mapIcon = mapIcon;
        this.mapWeight = mapWeight;
        this.position = lastPos = position;

        System.out.println("Created " + toString());
    }

    public BaseMap getMap() {
        return map;
    }

    public char getMapIcon() {
        return this.mapIcon;
    }

    public char getMapWeight() {
        return mapWeight;
    }

    public Position getPos() {
        return position;
    }

    public Position getLastPos() {
        return lastPos;
    }

    @Override
    public String toString() {
        return name + " [#" + getId()
            + " | " + getMapIcon()
            + " | w=" + getMapWeight()
            + " | at " + getPos()
            + " of map " + getMap().getId()
            + "]";
    }
}
