package rpgram.ui;

public enum IconType {
    // @formatter:off

    emptiness("#"),
    defaultPlayer("@"),
    hare("&"),
    grass("\""),
    tree(" ^ "
       + "/|\\"
       + "\\|/"
    ),
    rock("*"),
    house(
        " ^ " +
        "/#\\" +
        "|0|"
    ),
    village(".^."
          + "^.."
          + "^.^"
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
