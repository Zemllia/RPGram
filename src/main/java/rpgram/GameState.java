package rpgram;

import com.crown.BaseGameState;
import com.crown.common.utils.Random;
import com.crown.creatures.Organism;
import com.crown.maps.Map;
import com.crown.maps.Point3D;
import rpgram.creatures.Human;
import rpgram.creatures.Wanderer;
import rpgram.maps.MapLevel;

/**
 * Contains game state for current running session.
 */
public class GameState extends BaseGameState {
    public GameState(Map globalMap) {
        super(globalMap);
    }

    public Human addPlayer(long telegramId, String name) {
        Point3D pt = Random.getFreePoint(getGlobalMap(), new Point3D(-1, -1, MapLevel.ground + 1));
        var p = new Wanderer(telegramId, name, getGlobalMap(), pt);
        players.add(p);
        return p;
    }

    public Human getPlayer(long id) {
        for (Organism creature : players) {
            if (creature instanceof Human && ((Human) creature).telegramId == id) {
                return (Human) creature;
            }
        }
        return null;
    }

    public boolean hasPlayer(long id) {
        return getPlayer(id) != null;
    }
}
