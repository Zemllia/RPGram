package rpgram.maps;

public class BaseMap extends NamedObject {
    protected final int areaWidth;
    protected final int areaHeight;

    protected final char[][][] mapContainer;

    protected final char[][] weightsOfObjects = {
        { ' ', '1' },
        { 'F', '2' },
        { 'Y', '3' },
        { 'R', '0' },
        { MapLegend.SWAMP.getValue(), '5' }
    };

    public BaseMap(int id, String name, int areaWidth, int areaHeight) {
        super(id, name);
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        mapContainer = new char[MapLayer.values().length][areaHeight][areaWidth];
    }

    public int getAreaWidth() {
        return areaWidth;
    }

    public int getAreaHeight() {
        return areaHeight;
    }

    public char[][] layer(MapLayer l) {
        return mapContainer[l.ordinal()];
    }

    public char getChar(MapLayer l, Position pos) {
        return layer(l)[pos.y][pos.x];
    }

    public void setChar(MapLayer l, Position pos, char value) {
        layer(l)[pos.y][pos.x] = value;
    }

    public String viewMapArea(Position pos, int radius) {
        System.out.println("Show map area for x=" + pos.x + " y=" + pos.y + " r=" + radius);

        int diameter = radius * 2;
        char[][] renderArray = new char[diameter][diameter];

        int diffX = 0;
        int diffY = 0;
        if (pos.x - radius < 0) {
            diffX = Math.abs(pos.x - radius);
        }
        if (pos.y - radius < 0) {
            diffY = Math.abs(pos.y - radius);
        }

        for (int t = 0; t < MapLayer.values().length; t++) {
            for (int y = 0; y < diameter; y++) {
                for (int x = 0; x < diameter; x++) {
                    if (y > diffY - 1 && x > diffX - 1) {
                        if (mapContainer[t][pos.y + y - radius][pos.x + x - radius] != 0) {
                            renderArray[y][x] = mapContainer[t][pos.y + y - radius][pos.x + x - radius];
                        }
                    }
                }
            }
        }
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                if (renderArray[y][x] == 0) {
                    renderArray[y][x] = MapLegend.WALL.getValue();
                }
            }
        }

        StringBuilder answer = new StringBuilder();
        answer.append("<code>\n");
        answer.append("\n");
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                answer.append(renderArray[y][x]).append("  ");
            }
            answer.append("\n");
        }
        answer.append("\n</code>");
        return answer.toString();
    }

    public void addObject(GameObject obj) {
        setChar(MapLayer.PLAYERS, obj.getPos(), obj.getMapIcon());
        setChar(MapLayer.WEIGHTS, obj.getPos(), obj.getMapWeight());
    }

    public void moveObject(GameObject obj) {
        if (isValidPoint(obj.getPos())) {
            if (isValidPoint(obj.getLastPos())) {
                setChar(MapLayer.PLAYERS, obj.getLastPos(), (char) 0);
                setChar(MapLayer.WEIGHTS, obj.getLastPos(), recalculateCellWeight(obj.getLastPos()));
            }

            setChar(MapLayer.PLAYERS, obj.getPos(), obj.getMapIcon());
            setChar(MapLayer.WEIGHTS, obj.getPos(), obj.getMapWeight());
        }
    }

    protected void clearMap() {
        for (int y = 0; y < areaHeight; y++) {
            for (int x = 0; x < areaWidth; x++) {
                layer(MapLayer.GROUND)[y][x] = ' ';
                layer(MapLayer.CAVES)[y][x] = ' ';
            }
        }
    }

    protected char recalculateCellWeight(Position pos) {
        int curWeight = 0;
        for (int i = 0; i < MapLayer.values().length; i++) {
            char checkChar = mapContainer[i][pos.y][pos.x];
            for (int j = 0; i < weightsOfObjects.length; i++) {
                if (checkChar == weightsOfObjects[j][0]) {
                    if (curWeight < Character.getNumericValue(weightsOfObjects[i][1])) {
                        curWeight = Character.getNumericValue(weightsOfObjects[i][1]);
                    }
                }
            }
        }
        return (char) (curWeight + '0');
    }

    public boolean isValidPoint(Position pos) {
        return pos.x >= 0 && pos.x <= areaWidth && pos.y >= 0 && pos.y <= areaHeight;
    }
}
