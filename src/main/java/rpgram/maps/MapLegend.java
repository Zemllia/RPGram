package rpgram.maps;

public enum MapLegend {
    CHEST('=', "Chest"),
    WALL('#', "Wall"),
    TREE('^', "Tree"),
    SWAMP('%', "Swamp"),
    ROCK('*', "A rock, or a big stone"),
    HOLE('o', "Ground hole"),
    VILLAGE('v', "Village"),
    TREASURE('x', "Treasure");

    private final char value;
    private final String description;

    MapLegend(char value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * Get value of a map object.
     */
    public char getValue() {
        return this.value;
    }

    /**
     * Get description of a map object.
     */
    public String getDescription() {
        return this.description;
    }
}