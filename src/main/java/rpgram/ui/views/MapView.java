package rpgram.ui.views;

import com.crown.maps.MapObject;
import com.crown.maps.Point3D;
import rpgram.creatures.Human;
import rpgram.maps.MapLevel;
import rpgram.maps.TextMapIcon;

import java.util.HashSet;

public class MapView {
    public static String markup(Human player) {
        return statsToMarkup(player) + "\n" + mapToMarkup(player);
    }

    private static String statsToMarkup(Human player) {
        return "🧭 " + (player.getPt0().x + 1) + ", " + (player.getPt0().y + 1)
            + "  ⭐ " + player.getLevel() + "/" + player.getXp()
            + "  ♥ " + player.getHp()
            + "  ⚡ " + player.getEnergy();
    }

    private static String mapToMarkup(Human player) {
        var map = mapToCharMatrix(player);
        StringBuilder answer = new StringBuilder("<code>\n");

        for (var row : map) {
            for (var c : row) {
                answer.append(c).append(" ");
            }
            answer.append("\n");
        }
        answer.append("\n</code>");
        return answer.toString();
    }

    private static char[][] mapToCharMatrix(Human player) {
        Point3D centerPoint = player.getPt0().withZ(MapLevel.height - 1);
        int radius = player.getFov();
        var absAreaZero = centerPoint.minus(new Point3D(radius, radius, 0));
        var objects = player.getMap().getRaw3DArea(centerPoint, radius);
        var map = new char[objects[0].length][objects[0][0].length];
        var largeObjs = new HashSet<LargeMapObjectContainer>();
        for (int relZ = 0; relZ < objects.length; relZ++) {
            for (int relY = 0; relY < objects[relZ].length; relY++) {
                for (int relX = 0; relX < objects[relZ][relY].length; relX++) {
                    MapObject mapObj = objects[relZ][relY][relX];
                    if (mapObj != null && mapObj.getMap() != null) {
                        var largeObj = largeObjs.stream()
                            .filter(c -> c.obj == mapObj)
                            .findFirst()
                            .orElse(null);
                        if (largeObj != null
                            && largeObj.fillingCellIdx < largeObj.cellsCount - 1) {
                            // object is already placed on map; continue drawing
                            if (largeObj.fillingCellIdx == largeObj.cellsCount - 1) {
                                // all parts of large object have been drawn
                                largeObjs.remove(largeObj);
                            }
                        } else {
                            // new object
                            var w = mapObj.getWidth();
                            var h = mapObj.getHeight();
                            if (w > 1 || h > 1) {
                                // save large objects to the containers
                                largeObjs.add(new LargeMapObjectContainer(mapObj));
                            }
                        }
                        var icon = getParticleByPoint(
                            mapObj,
                            new Point3D(
                                absAreaZero.x + relX,
                                absAreaZero.y + relY,
                                0
                            )
                        );
                        if (icon != ' ' || map[relY][relX] == '\0') {
                            map[relY][relX] = icon;
                        }
                    } else if (map[relY][relX] == '\0') {
                        map[relY][relX] = ((TextMapIcon) player.getMap().getEmptyIcon()).get().charAt(0);
                    }
                }
            }
        }
        // Fix map shrink by Telegram
        var lastChar = map[map.length - 1][map[0].length - 1];
        if (Character.isSpaceChar(lastChar) || lastChar == '\0') {
            map[map.length - 1][map[0].length - 1] = '.';
        }
        return map;
    }

    private static char getParticleByPoint(MapObject obj, Point3D pt) {
        var particles = obj.getParticles();
        var part = '\0';
        for (int i = 0; i < particles.length; i++) {
            if (particles[i].x == pt.x && particles[i].y == pt.y) {
                part = ((TextMapIcon) obj.getMapIcon()).get().charAt(i);
            }
        }
        return part;
    }

    static class LargeMapObjectContainer {
        final MapObject obj;
        final int cellsCount;
        int fillingCellIdx = 0;

        LargeMapObjectContainer(MapObject obj) {
            this.obj = obj;
            cellsCount = obj.getParticles().length;
        }
    }
}
