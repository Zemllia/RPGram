package rpgram.ui;

public enum IconType {
    // @formatter:off

    emptiness("#"),
    defaultPlayer("@"),
    grass(" "),
    tree(" ^ \n"
       + "/|\\\n"
       + " | "
    ),
    rock("*"),
    house(
        " ^  \n" +
        "/#\\\n" +
        "|0|\n"
    ),
    village(".^.\n"
          + "^..\n"
          + "^.^\n"
    );

    // @formatter:on

    private final String representation;

    IconType(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }
}
