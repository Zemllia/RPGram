package rpgram;

import com.crown.BaseGameState;
import com.crown.common.utils.Random;
import com.crown.creatures.Organism;
import com.crown.maps.Map;
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
        var pt = Random.getFreePoint(getGlobalMap());
        var column = getGlobalMap().getColumn(pt.withZ(MapLevel.height));
        int placementHeight = 0;
        for (int i = 0; i < column.length; i++) {
            placementHeight++;
            if (column[i] != null && !column[i].isWalkable()
                && i < column.length - 1
                && (column[i + 1] == null || column[i + 1].isWalkable())) {
                break;
            }
        }
        var p = new Wanderer(telegramId, name, getGlobalMap(), pt.withZ(placementHeight));
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
