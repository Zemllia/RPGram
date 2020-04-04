package rpgram.core;

public class Position {
    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position add(int deltaX, int deltaY) {
        return new Position(x + deltaX, y + deltaY);
    }
}
