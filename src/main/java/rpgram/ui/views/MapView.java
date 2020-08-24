package rpgram.ui.views;

import com.crown.i18n.I18n;
import com.crown.i18n.ITemplate;
import com.crown.maps.MapObject;
import com.crown.maps.Point3D;
import rpgram.Main;
import rpgram.creatures.Human;
import rpgram.maps.MapLevel;
import rpgram.maps.TextMapIcon;

public class MapView {
    public static String markup(Human player, ITemplate message) {
        var result = statsToMarkup(player) + "\n";
        if (message != I18n.okMessage) {
            result += message;
        }
        return result + mapToMarkup(player);
    }

    public static String markup(Human player) {
        return statsToMarkup(player) + "\n" + mapToMarkup(player);
    }

    private static String statsToMarkup(Human player) {
        var p = player.getPt0();
        return "🧭 " + (p.x + 1) + ", " + (p.y + 1) + ", " + (p.z + 1)
            + "  ⭐ " + player.getLevel() + "/" + player.getXp()
            + "  ♥ " + player.getHp()
            + "  ⚡ " + player.getEnergy();
    }

    private static String mapToMarkup(Human player) {
        StringBuilder answer = new StringBuilder("<code>\n");
        var map = mapToCharMatrix(player);
        for (var row : map) {
            for (var c : row) {
                answer.append(c).append(" ");
            }
            answer.append("\n");
        }
        if (Main.config.isDebugEnabled()) {
            var m = player.getMap();
            var p = player.getPt0();
            var left = m.get(p.minus(1, 0, 0));
            answer.append("← ").append(shortName(left)).append("\t");
            var right = m.get(p.plus(1, 0, 0));
            answer.append("→ ").append(shortName(right)).append("\n");
            var up = m.get(p.minus(0, 1, 0));
            answer.append("↑ ").append(shortName(up)).append("\t");
            var down = m.get(p.plus(0, 1, 0));
            answer.append("↓ ").append(shortName(down)).append("\n");
            var below = m.get(p.minus(0, 0, 1));
            answer.append("below: ").append(shortName(below)).append("\t");
            var above = m.get(p.plus(0, 0, 1));
            answer.append("above: ").append(shortName(above)).append("\n");
        }
        answer.append("</code>");
        return answer.toString();
    }

    private static String shortName(MapObject obj) {
        if (obj == null) return "none";
        return obj.getKeyName() + " " + obj.getPt0();
    }

    private static char[][] mapToCharMatrix(Human player) {
        var emptyChar = ((TextMapIcon) player.getMap().getEmptyIcon()).get().charAt(0);
        Point3D centerPoint = player.getPt0().withZ(MapLevel.height - 1);
        int radius = player.getFov();
        var absAreaZero = centerPoint.minus(new Point3D(radius, radius, 0));
        var objects = player.getMap().getRaw3DArea(centerPoint, radius);
        var map = new char[objects[0].length][objects[0][0].length];
        for (MapObject[][] layer : objects) {
            for (int relY = 0; relY < layer.length; relY++) {
                for (int relX = 0; relX < layer[relY].length; relX++) {
                    MapObject mapObj = layer[relY][relX];
                    if (mapObj != null) {
                        var icon = getParticleByPoint(
                            mapObj,
                            absAreaZero.plus(relX, relY, 0)
                        );
                        if (icon != ' ' || map[relY][relX] == '\0') {
                            map[relY][relX] = icon;
                        }
                    }
                }
            }
        }
        // replace \0-s with map-end chars
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '\0') map[y][x] = emptyChar;
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
}
