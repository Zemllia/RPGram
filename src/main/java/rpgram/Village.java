import core.Position;
import core.utils.Random;

public class Village {
    int villageID;
    String villageName;
    String ownerName;
    int ownerID;
    //TODO Сделать возможность добавлять совладельцев (Строить в деревне могут только владельцы);
    int[] coOwnersID;
    //TODO Сделать возможность прописаться в поселении и спавн рандомных NPC
    int villagersCount;
    boolean isBuildedByPlayer;

    Position villagePos;

    char[][][] villageMap = new char[MapLayers.values().length][50][50];
    char[][] weightsOfObjects = { { ' ', '1' }, { 'F', '2' }, { 'T', '3' }, { 'R', '0' }, { '%', '5' } };


    public Village(int villageID, String villageName, String ownerName, int ownerID, boolean isBuildedByPlayer, Position villagePos) {
        this.villageID = villageID;
        this.villageName = villageName;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.isBuildedByPlayer = isBuildedByPlayer;
        this.villagePos = villagePos;
        generateVillage();
        System.out.println("Деревня сгенерировалась x=" + villagePos.x + " y=" + villagePos.y);
    }

    void generateVillage() {
        clearMap();
        generateEnvironment();
        if (!isBuildedByPlayer) {
            generateBrokenHouses();
        }
    }

    char recalculateCellWeight(Position pos) {
        int curWeight = 0;
        for (int i = 0; i < MapLayers.values().length; i++) {
            char checkChar = villageMap[i][pos.x][pos.y];
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

    private void generateBrokenHouses() {
        int numberOfHouses = Random.randInt(3, 8);
        for (int i = 0; i < numberOfHouses; i++) {
            villageMap[MapLayers.ENVIRONMENT.ordinal()][Random.randInt(0, 49)][Random.randInt(0, 49)] = 'D';
        }
    }

    private void generateEnvironment() {
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                int randChance = Random.randInt(0, 100);
                if (randChance < 50) {
                    villageMap[MapLayers.ENVIRONMENT.ordinal()][i][j] = '^';
                }
                if (randChance > 50 && randChance < 70) {
                    villageMap[MapLayers.ENVIRONMENT.ordinal()][i][j] = 'o';
                }
            }
        }
    }

    private void clearMap() {
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                villageMap[MapLayers.GROUND.ordinal()][i][j] = ' ';
                villageMap[MapLayers.CAVES.ordinal()][i][j] = ' ';
                villageMap[MapLayers.WEIGHTS.ordinal()][i][j] = '1';
            }
        }
    }

    public char[][][] getMap() {
        return villageMap;
    }

    public int getVillageID() {
        return villageID;
    }

    public String getVillageName() {
        return villageName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getVillagersCount() {
        return villagersCount;
    }

    public void setVillagePos(Position pos) {
        villagePos = pos;
    }
}
