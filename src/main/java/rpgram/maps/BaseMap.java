package rpgram.maps;

import rpgram.core.GameObject;
import rpgram.core.NamedObject;
import rpgram.core.Position;

public class BaseMap extends NamedObject {
    protected int areaWidth;
    protected int areaHeight;

    protected char[][][] mapContainer;

    protected char[][] weightsOfObjects = {
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
        mapContainer = new char[MapLayers.values().length][areaWidth][areaHeight];
    }

    public int getAreaWidth() {
        return areaWidth;
    }

    public int getAreaHeight() {
        return areaHeight;
    }

    public char[][] layer(MapLayers l) {
        return mapContainer[l.ordinal()];
    }

    public char getChar(MapLayers l, Position pos) {
        return layer(l)[pos.x][pos.y];
    }

    public void setChar(MapLayers l, Position pos, char value) {
        layer(l)[pos.x][pos.y] = value;
    }

    public String viewMapArea(Position pos, int radius) {
        System.out.println("Show map area for x=" + pos.x + " y=" + pos.y + " r=" + radius);

        int diameter = radius * 2;
        char[][] renderArray = new char[diameter][diameter];

        for (int t = 0; t < MapLayers.values().length; t++) {
            int diffX = 0;
            int diffY = 0;
            if (pos.x - radius < 0) {
                diffX = Math.abs(pos.x - radius);
            }
            if (pos.y - radius < 0) {
                diffY = Math.abs(pos.y - radius);
            }
            for (int i = 0; i < diameter; i++) {
                for (int j = 0; j < diameter; j++) {
                    if (i > diffX - 1 && j > diffY - 1) {
                        if (mapContainer[t][pos.x + i - radius][pos.y + j - radius] != 0) {
                            renderArray[i][j] = mapContainer[t][pos.x + i - radius][pos.y + j - radius];
                        }
                    }
                }
            }
        }
        for (int i = 0; i < diameter; i++) {
            for (int j = 0; j < diameter; j++) {
                if (renderArray[i][j] == 0) {
                    renderArray[i][j] = MapLegend.WALL.getValue();
                }
            }
        }

        StringBuilder answer = new StringBuilder();
        answer.append("\nСудя по карте моя позиция - x=").append(pos.x).append(" y=").append(pos.y);
        answer.append("<code>\n");
        answer.append("\n");
        for (int i = 0; i < radius * 2; i++) {
            for (int j = 0; j < radius * 2; j++) {
                // show player as white letter (but breaks alignment)
                // if (renderArray[i][j] <= 127) {
                answer.append(renderArray[i][j]).append("  ");
                // } else {
                //     answer.append("</code>").append(renderArray[i][j]).append("<code>");
                // }
            }
            answer.append("\n");
        }
        answer.append("\n</code>");
        return answer.toString();
    }

    public void addObject(GameObject obj) {
        setChar(MapLayers.PLAYERS, obj.getPos(), obj.getMapIcon());
        setChar(MapLayers.WEIGHTS, obj.getPos(), obj.getMapWeight());
    }

    public void moveObject(GameObject obj) {
        if (isValidPoint(obj.getPos())) {
            if (isValidPoint(obj.getLastPos())) {
                setChar(MapLayers.PLAYERS, obj.getLastPos(), (char) 0);
                setChar(MapLayers.WEIGHTS, obj.getLastPos(), recalculateCellWeight(obj.getLastPos()));
            }

            setChar(MapLayers.PLAYERS, obj.getPos(), obj.getMapIcon());
            setChar(MapLayers.WEIGHTS, obj.getPos(), obj.getMapWeight());
        }
    }

    protected void clearMap() {
        for (int i = 0; i < areaWidth; i++) {
            for (int j = 0; j < areaHeight; j++) {
                layer(MapLayers.GROUND)[i][j] = ' ';
                layer(MapLayers.CAVES)[i][j] = ' ';
            }
        }
    }

    protected char recalculateCellWeight(Position pos) {
        int curWeight = 0;
        for (int i = 0; i < MapLayers.values().length; i++) {
            char checkChar = mapContainer[i][pos.x][pos.y];
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
