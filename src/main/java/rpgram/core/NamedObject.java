package rpgram.core;

public class NamedObject {
    private final int id;
    protected String name;

    public NamedObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }
}
