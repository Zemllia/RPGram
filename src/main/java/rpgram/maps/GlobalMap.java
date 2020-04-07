package rpgram.maps;

import rpgram.Treasure;
import rpgram.core.I18N;
import rpgram.core.NamesGenerator;
import rpgram.core.Position;
import rpgram.core.utils.Random;

import java.util.ArrayList;

public class GlobalMap extends BaseMap {
    private final int villagesCount;
    private final int swampCount;

    public final ArrayList<VillageMap> villages = new ArrayList<>();
    public final ArrayList<Treasure> treasures = new ArrayList<>();

    public GlobalMap(int areaWidth, int areaHeight, int villagesCount, int swampCount) {
        super(I18N.get("map.global.name"), areaWidth, areaHeight);
        this.villagesCount = villagesCount;
        this.swampCount = swampCount;
        generateMap();
        System.out.println("World generation completed.");
    }

    void generateMap() {
        clearMap();

        for (int i = 0; i < areaWidth; i++) {
            for (int j = 0; j < areaHeight; j++) {
                int randChance = Random.randInt(0, 100);
                if (randChance < 30) {
                    layer(MapLayer.ENVIRONMENT)[i][j] = MapLegend.TREE.getValue();
                } else if (randChance > 30 && randChance < 35) {
                    layer(MapLayer.ENVIRONMENT)[i][j] = MapLegend.ROCK.getValue();
                }
            }
        }

        for (int i = 0; i < swampCount; i++) {
            generateSwamp();
        }
        for (int i = 0; i < villagesCount; i++) {
            generateVillage(i);
        }
        //generateRoadsBetweenVillages();
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
                    layer(MapLayer.ENVIRONMENT)[i][(j + randomOffset) % areaWidth] = MapLegend.SWAMP.getValue();
                    verticalScale += Random.randInt(0, 2);
                }
            } else {
                for (int j = startPointY; j <= startPointY + verticalScale; j++) {
                    layer(MapLayer.GROUND)[i][(j + randomOffset) % areaWidth] = MapLegend.SWAMP.getValue();
                    verticalScale -= Random.randInt(0, 2);
                    if (verticalScale <= 1) {
                        verticalScale = 1;
                    }
                }
            }
        }
    }

    private void generateVillage(int id) {
        Position vPos = new Position(Random.randInt(0, areaHeight), Random.randInt(0, areaHeight));
        VillageMap v = new VillageMap(
            this,
            id,
            NamesGenerator.getRandomVillageName(),
            NamesGenerator.getRandomNpcName(),
            Random.randInt(0, 9999),
            false,
            vPos
        );

        setChar(MapLayer.ENVIRONMENT, vPos, 'v');
        villages.add(v);
    }

//    private void generateRoadsBetweenVillages() {
//        PathFinding pathFinding = new PathFinding();
//        for (VillageMap village : villages) {
//            for (int i = 0; i <= Random.randInt(0, 2); i++) {
//                VillageMap villageForRoad = villages.get(Random.randInt(0, villagesCount - 1));
//                while (villageForRoad.getId() == village.getId()) {
//                    villageForRoad = villages.get(Random.randInt(0, villagesCount - 1));
//                }
//                ArrayList<Position> roadPath = pathFinding.findPath(village.getPosition(), villageForRoad.getPosition(), this);
//
//                for (Position roadElement : roadPath) {
//                    setChar(MapLayers.ground, roadElement, '◼');
//                }
//            }
//        }
//    }

    public VillageMap checkVillage(Position villagePos) {
        for (VillageMap village : villages) {
            if (village.getPosition().x == villagePos.x && village.getPosition().y == villagePos.y) {
                return village;
            }
        }
        System.out.println("DEBUG: village not found");
        return null;
    }
}
