import core.NamesGenerator;
import core.Position;
import core.utils.Random;

import java.util.ArrayList;

public class BaseMap {
    int areaWidth;
    int areaHeight;

    char[][][] gameMap;
    private char[][] weightsOfObjects = { { ' ', '1' }, { 'F', '2' }, { 'Y', '3' }, { 'R', '0' }, { '%', '5' } };

    ArrayList<Village> villages = new ArrayList<>();
    ArrayList<Treasure> treasures = new ArrayList<>();

    public Map(int areaWidth, int areaHeight) {
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        gameMap = new char[MapLayers.values().length][areaWidth][areaHeight];
    }

    String viewMapArea(Position pos, int radius, int mapId) {
        System.out.println("Show map area for x=" + pos.x + " y=" + pos.y + " r=" + radius);

        char[][][] renderMap;
        if (mapId == -1) {
            renderMap = gameMap;
        } else {
            renderMap = villages.get(mapId).getMap();
        }
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
                        if (renderMap[t][pos.x + i - radius][pos.y + j - radius] != 0) {
                            renderArray[i][j] = renderMap[t][pos.x + i - radius][pos.y + j - radius];
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
                if (renderArray[i][j] <= 127) {
                    answer.append(renderArray[i][j]).append(" ");
                } else {
                    answer.append("</code>").append(renderArray[i][j]).append("<code>");
                }
            }
            answer.append("\n");
        }
        answer.append("\n</code>");
        return answer.toString();
    }

    private void clearMap() {
        for (int i = 0; i < areaWidth; i++) {
            for (int j = 0; j < areaHeight; j++) {
                gameMap[MapLayers.GROUND.ordinal()][i][j] = ' ';
                gameMap[MapLayers.CAVES.ordinal()][i][j] = ' ';
            }
        }
    }

    private char recalculateCellWeight(Position pos) {
        int curWeight = 0;
        for (int i = 0; i < MapLayers.values().length; i++) {
            char checkChar = gameMap[i][pos.x][pos.y];
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

    char getSymbolOnPosAndLayer(Position position, int layer) {
        return gameMap[layer][position.x][position.y];
    }
}
