package rpgram;

import rpgram.core.Position;
import rpgram.core.utils.Random;
import rpgram.maps.BaseMap;
import rpgram.maps.MapLayers;

public class Village extends BaseMap {
    int id;
    String name;

    int ownerID;
    String ownerName;
    //TODO Сделать возможность добавлять совладельцев (Строить в деревне могут только владельцы);
    int[] coOwnersID;
    //TODO Сделать возможность прописаться в поселении и спавн рандомных NPC
    int villagersCount;
    boolean isBuiltByPlayer;

    Position position;

    public Village(int id, String name, String ownerName, int ownerID, boolean isBuiltByPlayer, Position position) {
        super(50, 50);
        this.id = id;
        this.name = name;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.isBuiltByPlayer = isBuiltByPlayer;
        this.position = position;
        generateVillage();
        System.out.println("Деревня сгенерировалась x=" + position.x + " y=" + position.y);
    }

    void generateVillage() {
        clearMap();
        generateEnvironment();
        if (!isBuiltByPlayer) {
            generateBrokenHouses();
        }
    }

    private void generateBrokenHouses() {
        int numberOfHouses = Random.randInt(3, 8);
        for (int i = 0; i < numberOfHouses; i++) {
            layer(MapLayers.ENVIRONMENT)[Random.randInt(0, areaWidth)][Random.randInt(0, areaHeight)] = 'D';
        }
    }

    private void generateEnvironment() {
        for (int i = 0; i < areaWidth; i++) {
            for (int j = 0; j < areaHeight; j++) {
                int randChance = Random.randInt(0, 100);
                if (randChance < 50) {
                    layer(MapLayers.ENVIRONMENT)[i][j] = '^';
                }
                if (randChance > 50 && randChance < 70) {
                    layer(MapLayers.ENVIRONMENT)[i][j] = 'o';
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getVillagersCount() {
        return villagersCount;
    }
}
