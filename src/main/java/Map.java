import core.NamesGenerator;
import core.Position;
import core.utils.Random;

import java.util.ArrayList;

public class Map {
    int maxXBound;
    int maxYBound;

    enum Layer {
        WEIGHTS,
        CAVES,
        GROUND,
        ENVIRONMENT,
        PLAYERS,
        HEAVENS
    }

    int villagesCount;
    int cavesCount;
    int swampCount;

    char [][][] gameMap;
    char [][] weightsOfObjects = {{' ', '1'}, {'F', '2'}, {'T', '3'}, {'R', '0'}, {'%', '5'}};



    ArrayList<Village> villages = new ArrayList<Village>();

    public Map (int maxXBound, int maxYBound, int villagesCount, int cavesCount, int swampCount){
        this.maxXBound = maxXBound;
        this.maxYBound = maxYBound;
        this.villagesCount = villagesCount;
        this.cavesCount = cavesCount;
        this.swampCount = swampCount;
        gameMap = new char[Layer.values().length][maxXBound][maxYBound];
    }

    void generateMap(){
        clearMap();
        generateSomeEnvironment();
        generateSwamp();
        generateSomeVillages();
    }

    String viewMapArea (Position pos, int radius, int mapId) {

        char[][][] renderMap;

        if(mapId == -1){
            renderMap = gameMap;
        } else {
            renderMap = villages.get(mapId).getMap();
        }

        int diameter = radius * 2;

        char[][] renderArray = new char[diameter][diameter];

        String answer = "";

        String[] treesArray = {"\uD83C\uDF32"};
        // String[] flowersArray = {"\uD83C\uDF39", "\uD83C\uDF37", "\uD83C\uDF31"};

        System.out.println(pos.x+ "  " +  pos.y+ "   " + radius);

        for (int t = 0; t < Layer.values().length; t++) {
            int diffX = 0;
            int diffY = 0;
            if(pos.x - radius < 0){
                diffX = Math.abs(pos.x - radius);
            }
            if(pos.y - radius < 0){
                diffY = Math.abs(pos.y - radius);
            }
            for (int i = 0; i < diameter; i++){
                for (int j = 0; j < diameter; j++) {
                    if (i > diffX-1 && j > diffY-1) {
                        if (renderMap[t][pos.x + i - radius][pos.y + j - radius] != 0) {
                            renderArray[i][j] = renderMap[t][pos.x + i - radius][pos.y + j - radius];
                        }
                    }
                }
            }
        }
        for(int i = 0; i < diameter; i++) {
            for(int j = 0; j < diameter; j++) {
                if(renderArray[i][j] == 0) {
                    renderArray[i][j] = 'N';
                }
            }
        }
        answer = answer + "<code>\n";
        answer = answer + "\n";
        for (int i = 0; i < radius * 2; i++) {
            for (int j = 0; j < radius * 2; j++) {
                if (renderArray[i][j] <= 127) {
                    if(renderArray[i][j] == 'T'){
                        answer += "</code>" +  treesArray[Random.randInt(0, treesArray.length-1)] + "<code>";
                    } else if (renderArray[i][j] == 'N'){
                        answer += "</code>" +  "\uD83C\uDF2B" + "<code>";
                    } else if (renderArray[i][j] == 'V') {
                        answer += "</code>" +  "\uD83C\uDFD8" + "<code>";
                    } else if (renderArray[i][j] == 'D') {
                        answer += "</code>" +  "\uD83C\uDFDA" + "<code>";
                    } else {
                        answer += renderArray[i][j] + " ";
                    }
                } else {
                    answer += "</code>" + renderArray[i][j] + "<code>";
                }
            }
            answer = answer + "\n";
        }
        answer = answer + "\n</code>";
        answer = answer + "\nСудя по карте моя позиция - x=" + pos.x + " y=" + pos.y;
        return answer;
    }

    void instantiateNewPlayer(Position pos, char playerChar, int mapId){
        char[][][] curMap;

        if(mapId == -1){
            curMap = gameMap;
        } else {
            curMap = villages.get(mapId).getMap();
        }
        curMap[Layer.PLAYERS.ordinal()][pos.x][pos.y] = playerChar;
        curMap[Layer.WEIGHTS.ordinal()][pos.x][pos.y] = '9';
    }

    void changePlayerPos (Position oldPos, Position newPos, char playerChar, int mapId) {
        char[][][] curMap;

        if(mapId == -1){
            curMap = gameMap;
        } else {
            curMap = villages.get(mapId).getMap();
        }
        if(newPos.x >= 0 && newPos.x <= maxXBound && newPos.y >= 0 && newPos.y <= maxYBound) {
            curMap[Layer.PLAYERS.ordinal()][oldPos.x][oldPos.y] = 0;
            curMap[Layer.PLAYERS.ordinal()][newPos.x][newPos.y] = playerChar;
            curMap[Layer.WEIGHTS.ordinal()][oldPos.x][oldPos.y] = recalculateCellWeight(oldPos);
            curMap[Layer.WEIGHTS.ordinal()][newPos.x][newPos.y] = '9';
        }
    }

    private void generateSomeEnvironment(){
        for (int i = 0; i < maxXBound; i++) {
            for (int j = 0; j < maxYBound; j++) {
                int randChance = Random.randInt(0, 100);
                if (randChance < 50) {
                    gameMap[Layer.ENVIRONMENT.ordinal()][i][j] = 'T';
                }
            }
        }
    }

    private void generateSomeVillages(){
        for(int i = 0; i < villagesCount; i++){
            Position vilgPosition = new Position(Random.randInt(0, maxYBound), Random.randInt(0, maxYBound));
            Village vilg = new Village(
                    i,
                    NamesGenerator.villageNames[Random.randInt(0, NamesGenerator.villageNames.length - 1)],
                    NamesGenerator.npcNames[Random.randInt(0, NamesGenerator.npcNames.length-1)],
                    Random.randInt(0, 9999),
                    false,
                    vilgPosition
            );
            gameMap[Layer.ENVIRONMENT.ordinal()][vilg.villagePos.x][vilg.villagePos.y] = 'V';
            villages.add(vilg);
        }
    }

    private void generateSwamp() {
        int swampXMax = 10;
        int swampXMin = 8;

        int startPointX = Random.randInt(0, maxXBound);
        int startPointY = Random.randInt(0, maxYBound);
        int endPointX = Random.randInt(swampXMin, swampXMax);

        System.out.println("Swamp generated at x=" + startPointX + " y=" + startPointY);

        for (int i = startPointX; i <= startPointX + endPointX; i++) {
            int verticalScale = 1;
            int randomOffset = Random.randInt(-2, 2);
            if (i <= (startPointX + endPointX) / 2) {
                for (int j = startPointY; j <= startPointY + verticalScale; j++) {
                    gameMap[Layer.ENVIRONMENT.ordinal()][i][(j + randomOffset) % maxXBound] = '%';
                    verticalScale += Random.randInt(0,2);
                }
            } else {
                for (int j = startPointY; j <= startPointY + verticalScale; j++) {
                    gameMap[Layer.GROUND.ordinal()][i][(j + randomOffset) % maxXBound] = '%';
                    verticalScale -= Random.randInt(0, 2);
                    if (verticalScale <= 1) {
                        verticalScale = 1;
                    }
                }
            }
        }
    }

    char recalculateCellWeight(Position pos){
        int curWeight = 0;
        for(int i = 0; i < Layer.values().length; i++){
            char checkChar = gameMap[i][pos.x][pos.y];
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

    private void clearMap(){
        for (int i = 0; i < maxXBound; i++) {
            for (int j = 0; j < maxYBound; j++) {
                gameMap[Layer.GROUND.ordinal()][i][j] = ' ';
                gameMap[Layer.CAVES.ordinal()][i][j] = ' ';
                gameMap[Layer.WEIGHTS.ordinal()][i][j] = '1';
            }
        }
    }

    public Village checkVillage(Position villagePos){
        for(Village item: villages){
            System.out.println("DEBUG: vlg item pos is x=" + item.villagePos.x + " y=" + item.villagePos.y);
            if(item.villagePos.x == villagePos.x && item.villagePos.y == villagePos.y){
                System.out.println("DEBUG: trying to return");
                return item;
            }
        }
        return null;
    }
}
