package rpgram;

public enum MapLegend {
    CHEST('=', "Chest"),
    WALL('#', "Wall"),
    TREE('^', "Tree"),
    SWAMP('%', "Swamp"),
    ROCK('*', "A rock or a big stone"),
    GROUNDHOLE('o', "Ground hole");

    private char value;
    private String description;

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