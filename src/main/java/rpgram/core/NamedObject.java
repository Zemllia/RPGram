package rpgram.core;

import rpgram.core.utils.Random;

public class NamedObject {
    private final int id;
    protected String name;

    public NamedObject(String name) {
        this.id = Random.randId();
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }
}
