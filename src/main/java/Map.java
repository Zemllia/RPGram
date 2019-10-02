import core.utils.Random;

public class Map {
    int maxXBound = 500;
    int maxYBound = 500;

    int villagesCount = 5;
    int cavesCount = 5;
    int swampCount = 3;

    char [][][] gameMap = new char[5][maxXBound][maxYBound];

    public Map (int maxXBound, int maxYBound, int villagesCount, int cavesCount, int swampCount){
        this.maxXBound = maxXBound;
        this.maxYBound = maxYBound;
        this.villagesCount = villagesCount;
        this.cavesCount = cavesCount;
        this.swampCount = swampCount;
    }

    void generateMap(){
        clearMap();
        generateSwamp();
    }

    private void generateSwamp() {
        int swampXMax = 10;
        int swampXMin = 8;

        int startPointX = Random.randInt(0, maxXBound);
        int startPointY = Random.randInt(0, maxYBound);
        int endPointX = Random.randInt(swampXMin, swampXMax);

        for (int i = startPointX; i <= startPointX + endPointX; i++) {
            int verticalScale = 1;
            int randomOffset = Random.randInt(-2, 2);
            if (i <= (startPointX + endPointX) / 2) {
                for (int j = startPointY; j <= startPointY + verticalScale; j++) {
                    gameMap[1][i][(j + randomOffset) % maxXBound] = '%';
                    verticalScale += Random.randInt(0,2);
                }
            } else {
                for (int j = startPointY; j <= startPointY + verticalScale; j++) {
                    gameMap[1][i][(j + randomOffset) % maxXBound] = '%';
                    verticalScale -= Random.randInt(0, 2);
                    if (verticalScale <= 1) {
                        verticalScale = 1;
                    }
                }
            }
        }
    }

    void generateVillages(){
        int counter = 0;

        for (int i = 0; i < villagesCount; i++){

        }
    }

    String viewMapArea (int xPos, int yPos, int radius) {

        char[][] renderArray = new char[radius * 2][radius * 2];

        String answer = "";

        for (int t = 0; t < 5; t++) {
            int diffX = 0;
            int diffY = 0;
            if(xPos - radius < 0){
                diffX = Math.abs(xPos - radius);
            }
            if(yPos - radius < 0){
                diffY = Math.abs(yPos - radius);
            }
            for (int i = 0; i < radius * 2; i++){
                for (int j = 0; j < radius * 2; j++) {
                    if (renderArray[i][j] == 0) {
                        if (i > diffX-1 && j > diffY-1) {
                            renderArray[i][j] = gameMap[4 - t][xPos + i - radius][yPos + j - radius];
                        }
                    }
                }
            }
        }
        for (int i = 0; i < radius * 2; i++) {
            for (int j = 0; j < radius * 2; j++){
                answer = answer + renderArray[i][j] + "  ";
            }
            answer = answer + "\n";
        }
        return answer;
    }

    void changePlayerPos (int oldPosX, int oldPosY, int newPosX, int newPosY) {
        if(newPosX >= 0 && newPosX <= maxXBound && newPosY >= 0 && newPosY <= maxYBound) {
            gameMap[4][oldPosX][oldPosY] = 0;
            gameMap[4][newPosY][newPosX] = '@';
        }
    }

    private void clearMap(){
        for (int i = 0; i < maxXBound; i++) {
            for (int j = 0; j < maxYBound; j++) {
                gameMap[1][i][j] = '*';
                gameMap[0][i][j] = '*';
            }
        }
    }
}
