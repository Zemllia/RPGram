package core;

public class GameObject {
    protected Position position;
    private int energy;
    private char mapIcon;
    private String name;

    public GameObject(String name, Position position,
                      int energy, char mapIcon) {

        this.name = name;
        this.position = position;
        this.energy = energy;
        this.mapIcon = mapIcon;
    }

    public char getMapIcon() {
        return this.mapIcon;
    }
    public String getName() {
        return this.name;
    }

    public int getEnergy() {
        return this.energy;
    }

    public void changeEnergy(int delta) {
        energy += delta;
    }

    public void move(Position delta) {
        position.x += delta.x;
        position.y += delta.y;
    }

}
