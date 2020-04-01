package rpgram.core;

import rpgram.maps.BaseMap;

public class GameObject extends NamedObject {
    protected BaseMap map;
    private char mapIcon;
    private char mapWeight;

    protected Position position;
    protected Position lastPos;

    private int energy;

    public GameObject(
        int id, String name, Position position,
        int energy, BaseMap map, char mapIcon,
        char mapWeight
    ) {
        super(id, name);
        this.position = lastPos = position;
        this.energy = energy;
        this.map = map;
        this.mapIcon = mapIcon;
        this.mapWeight = mapWeight;
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

    public int getEnergy() {
        return this.energy;
    }

    public void changeEnergy(int delta) {
        energy += delta;
    }
}
