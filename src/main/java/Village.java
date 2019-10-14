import core.Position;
import core.utils.Random;

public class Village {
    int villageID;
    String villageName;
    String ownerName;
    int ownerID;
    int[] coOwnersID;
    boolean isBuildedByPlayer;

    Position villagePos;

    enum Layer {
        WEIGHTS,
        CAVES,
        GROUND,
        ENVIRONMENT,
        PLAYERS,
        HEAVENS
    }

    char [][][] villageMap = new char[Map.Layer.values().length][50][50];
    char [][] weightsOfObjects = {{' ', '1'}, {'F', '2'}, {'T', '3'}, {'R', '0'}, {'%', '5'}};


    public Village (int villageID, String villageName, String ownerName, int ownerID, boolean isBuildedByPlayer) {
        this.villageID = villageID;
        this.villageName = villageName;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.isBuildedByPlayer = isBuildedByPlayer;
    }

    void generateVillage(){
        clearMap();
        generateEnvironment();
        if(!isBuildedByPlayer){
            generateBrokenHouses();
        }
    }

    char recalculateCellWeight(Position pos){
        int curWeight = 0;
        for(int i = 0; i < Map.Layer.values().length; i++){
            char checkChar = villageMap[i][pos.x][pos.y];
            for(int j = 0; i < weightsOfObjects.length; i++){
                if(checkChar == weightsOfObjects[j][0]){
                    if(curWeight < Character.getNumericValue(weightsOfObjects[i][1])){
                        curWeight = Character.getNumericValue(weightsOfObjects[i][1]);
                    }
                }
            }
        }
        return (char)(curWeight + '0');
    }

    private void generateBrokenHouses(){
        int numberOfHouses = Random.randInt(3,8);
        for (int i = 0; i < numberOfHouses; i++) {
            villageMap[Map.Layer.ENVIRONMENT.ordinal()][Random.randInt(0,50)][Random.randInt(0,50)] = 'D';
        }
    }

    private void generateEnvironment() {
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                int randChance = Random.randInt(0, 100);
                if (randChance < 50) {
                    villageMap[Map.Layer.ENVIRONMENT.ordinal()][i][j] = 'T';
                } else if(randChance > 50 && randChance < 60){
                    villageMap[Map.Layer.ENVIRONMENT.ordinal()][i][j] = 'F';
                }
            }
        }
    }

    private void clearMap() {
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                villageMap[Map.Layer.GROUND.ordinal()][i][j] = ' ';
                villageMap[Map.Layer.CAVES.ordinal()][i][j] = ' ';
                villageMap[Map.Layer.WEIGHTS.ordinal()][i][j] = '1';
            }
        }
    }
}
