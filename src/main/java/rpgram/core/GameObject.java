package rpgram.core;

import rpgram.maps.BaseMap;

public class GameObject extends NamedObject {
    protected BaseMap map;
    private char mapIcon;
    private char mapWeight;

    protected Position position;
    protected Position lastPos;

    public GameObject(
        int id, String name, BaseMap map, char mapIcon, char mapWeight, Position position
    ) {
        super(id, name);
        this.map = map;
        this.mapIcon = mapIcon;
        this.mapWeight = mapWeight;
        this.position = lastPos = position;

        System.out.println("Added " + name + " (#" + id + ") at x=" + position.x + " y=" + position.y);
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
}
