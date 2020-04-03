package rpgram.maps;

import rpgram.core.Position;
import rpgram.core.utils.Random;

public class VillageMap extends BaseMap {
    final int ownerID;
    private final BaseMap parentMap;
    final String ownerName;
    //TODO Сделать возможность добавлять совладельцев (Строить в деревне могут только владельцы);
    int[] coOwnersID;
    //TODO Сделать возможность прописаться в поселении и спавн рандомных NPC
    int villagersCount;
    final boolean isBuiltByPlayer;

    private final Position position;

    public VillageMap(BaseMap parentMap, int id, String name, String ownerName, int ownerID, boolean isBuiltByPlayer, Position position) {
        super(id, name, 50, 50);
        this.parentMap = parentMap;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.isBuiltByPlayer = isBuiltByPlayer;
        this.position = position;
        generateMap();
        System.out.println("Деревня сгенерировалась x=" + position.x + " y=" + position.y);
    }

    public Position getPosition() {
        return position;
    }

    void generateMap() {
        clearMap();
        generateEnvironment();
        if (!isBuiltByPlayer) {
            generateBrokenHouses();
        }
    }

    private void generateBrokenHouses() {
        int numberOfHouses = Random.randInt(3, 8);
        for (int i = 0; i < numberOfHouses; i++) {
            layer(MapLayer.ENVIRONMENT)[Random.randInt(0, areaWidth)][Random.randInt(0, areaHeight)] = 'D';
        }
    }

    private void generateEnvironment() {
        for (int i = 0; i < areaWidth; i++) {
            for (int j = 0; j < areaHeight; j++) {
                int randChance = Random.randInt(0, 100);
                if (randChance < 50) {
                    layer(MapLayer.ENVIRONMENT)[i][j] = '^';
                }
                if (randChance > 50 && randChance < 70) {
                    layer(MapLayer.ENVIRONMENT)[i][j] = 'o';
                }
            }
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getVillagersCount() {
        return villagersCount;
    }

    public BaseMap getParentMap() {
        return parentMap;
    }
}
