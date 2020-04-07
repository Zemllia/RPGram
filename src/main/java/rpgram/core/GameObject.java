package rpgram.core;

import rpgram.maps.BaseMap;
import rpgram.maps.MapWeight;

public class GameObject extends NamedObject {
    protected BaseMap map;
    private final char mapIcon;
    private final MapWeight mapWeight;

    protected Position position;
    protected Position lastPos;

    public GameObject(
        int id, String name, BaseMap map, char mapIcon, MapWeight mapWeight, Position position
    ) {
        super(id, name);
        this.map = map;
        this.mapIcon = mapIcon;
        this.mapWeight = mapWeight;
        this.position = lastPos = position;

        System.out.println("Created " + name + " (#" + id + ") at x=" + position.x + " y=" + position.y);
    }

    public BaseMap getMap() {
        return map;
    }

    public char getMapIcon() {
        return this.mapIcon;
    }

    public MapWeight getMapWeight() {
        return mapWeight;
    }

    public Position getPos() {
        return position;
    }

    public Position getLastPos() {
        return lastPos;
    }
}
