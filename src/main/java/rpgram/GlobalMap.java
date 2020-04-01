package rpgram;

import rpgram.core.NamesGenerator;
import rpgram.core.Position;
import rpgram.core.utils.Random;
import rpgram.maps.BaseMap;
import rpgram.maps.MapLayers;
import rpgram.maps.MapLegend;

import java.util.ArrayList;

public class GlobalMap extends BaseMap {
    private int villagesCount;
    private int swampCount;

    ArrayList<Village> villages = new ArrayList<>();
    ArrayList<Treasure> treasures = new ArrayList<>();

    public GlobalMap(int areaWidth, int areaHeight, int villagesCount, int swampCount) {
        super(areaWidth, areaHeight);
        this.villagesCount = villagesCount;
        this.swampCount = swampCount;
        gameMap = new char[MapLayers.values().length][areaWidth][areaHeight];
    }

    @Override
    public char[][][] getGameMapFromId(int mapId) {
        if (mapId == -1) {
            return gameMap;
        } else {
            return villages.get(mapId).getMap();
        }
    }

    void generateMap() {
        clearMap();

        for (int i = 0; i < areaWidth; i++) {
            for (int j = 0; j < areaHeight; j++) {
                int randChance = Random.randInt(0, 100);
                if (randChance < 30) {
                    gameMap[MapLayers.ENVIRONMENT.ordinal()][i][j] = MapLegend.TREE.getValue();
                } else if (randChance > 30 && randChance < 35) {
                    gameMap[MapLayers.ENVIRONMENT.ordinal()][i][j] = MapLegend.ROCK.getValue();
                }
            }
        }

        for (int i = 0; i < swampCount; i++) {
            generateSwamp();
        }
        for (int i = 0; i < villagesCount; i++) {
            generateVillage(i);
        }
        generateRoadsBetweenVillages();
    }

    private void generateSwamp() {
        int swampXMax = 10;
        int swampXMin = 8;

        int startPointX = Random.randInt(0, areaWidth);
        int startPointY = Random.randInt(0, areaHeight);
        int endPointX = Random.randInt(swampXMin, swampXMax);

        System.out.println("Swamp generated at x=" + startPointX + " y=" + startPointY);

        for (int i = startPointX; i <= startPointX + endPointX; i++) {
            int verticalScale = 1;
            int randomOffset = Random.randInt(-2, 2);
            if (i <= (startPointX + endPointX) / 2) {
                for (int j = startPointY; j <= startPointY + verticalScale; j++) {
                    gameMap[MapLayers.ENVIRONMENT.ordinal()][i][(j + randomOffset) % areaWidth] = MapLegend.SWAMP.getValue();
                    verticalScale += Random.randInt(0, 2);
                }
            } else {
                for (int j = startPointY; j <= startPointY + verticalScale; j++) {
                    gameMap[MapLayers.GROUND.ordinal()][i][(j + randomOffset) % areaWidth] = MapLegend.SWAMP.getValue();
                    verticalScale -= Random.randInt(0, 2);
                    if (verticalScale <= 1) {
                        verticalScale = 1;
                    }
                }
            }
        }
    }

    private void generateVillage(int id) {
        Position vPosition = new Position(Random.randInt(0, areaHeight), Random.randInt(0, areaHeight));
        Village v = new Village(
            id,
            NamesGenerator.villageNames[Random.randInt(0, NamesGenerator.villageNames.length - 1)],
            NamesGenerator.npcNames[Random.randInt(0, NamesGenerator.npcNames.length - 1)],
            Random.randInt(0, 9999),
            false,
            vPosition
        );
        gameMap[MapLayers.ENVIRONMENT.ordinal()][v.villagePos.x][v.villagePos.y] = 'v';
        villages.add(v);
    }

    private void generateRoadsBetweenVillages() {
        PathFinding pathFinding = new PathFinding();
        for (Village item : villages) {
            for (int i = 0; i <= Random.randInt(0, 2); i++) {
                Village villageForRoad = villages.get(Random.randInt(0, villagesCount - 1));
                while (villageForRoad.villageID == item.villageID) {
                    villageForRoad = villages.get(Random.randInt(0, villagesCount - 1));
                }
                ArrayList<Position> roadPath = pathFinding.findPath(item.villagePos, villageForRoad.villagePos, this);

                for (Position roadElement : roadPath) {
                    gameMap[MapLayers.GROUND.ordinal()][roadElement.x][roadElement.y] = '◼';
                }
            }
        }
    }

    Village checkVillage(Position villagePos) {
        for (Village item : villages) {
            System.out.println("DEBUG: vlg item pos is x=" + item.villagePos.x + " y=" + item.villagePos.y);
            if (item.villagePos.x == villagePos.x && item.villagePos.y == villagePos.y) {
                System.out.println("DEBUG: trying to return");
                return item;
            }
        }
        return null;
    }
}
