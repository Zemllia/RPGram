package rpgram.maps;

import rpgram.core.I18N;

public enum MapLegend {
    CHEST('='),
    WALL('#'),
    TREE('^'),
    SWAMP('%'),
    ROCK('*'),
    HOLE('o'),
    VILLAGE('v'),
    TREASURE('x');

    private final char value;
    private final String description;

    MapLegend(char value) {
        this.value = value;
        this.description = I18N.get("object.nominative." + name().toLowerCase());
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