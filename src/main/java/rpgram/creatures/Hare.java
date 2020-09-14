package rpgram.creatures;

import com.crown.common.utils.Random;
import com.crown.creatures.Organism;
import com.crown.items.InventoryItem;
import com.crown.maps.Map;
import com.crown.maps.*;
import com.crown.maps.vision.los.BresenhamLos;
import rpgram.items.Meat;
import rpgram.ui.IconType;
import rpgram.ui.MapIcons;

import java.util.*;

public class Hare extends Creature implements NonPlayable, Sighted {
    public Hare(Map map, Point3D pt) {
        super(
            "object.nominative.hare",
            map,
            MapIcons.getIcons().get(IconType.hare),
            MapWeight.OBSTACLE,
            pt,
            200,
            20,
            1,
            1
        );
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                makeAction();
            }
        }, 0, 1000);
    }

    public void makeAction() {
        var dangers = new ArrayList<MapObject>();
        // select all organisms in fov radius
        for (Organism d : getMap().getAll(
            Organism.class,
            getPt0(),
            getFov()
        )) {
            // Draw a sight line to the danger
            if (d instanceof Human
                && BresenhamLos.regular.exists(getMap(), getPt0(), d.getPt0()).getKey()) {
                dangers.add(d);
            }
        }
        if (dangers.size() > 0) {
            fleeFrom(dangers);
        }
    }

    private void fleeFrom(List<MapObject> dangers) {
        Point3D pt0 = getPt0();
        // make a set of available flee directions
        var directions = new HashSet<>(Arrays.asList(Direction.values()));
        // remove unavailable directions
        directions.remove(Direction.none);
        directions.remove(Direction.down);
        directions.remove(Direction.up);
        // remove directions causing npc to go out of map bounds.
        directions.removeIf(dir -> !getMap().isWalkable(pt0.plus(dir.point)));
        // remove directions that are occupied by dangers
        for (var danger : dangers) {
            var deltaPt = pt0.minus(danger.getPt0()).minus();
            var deltaDir = Direction.fromPoint(deltaPt);
            directions.remove(deltaDir);
        }
        // select random free direction
        var selectedDirIdx = Random.getInt(0, directions.size());
        int dirIdx = 0;
        Direction selectedDir = Direction.none;
        for (Direction dir : directions) {
            if (dirIdx == selectedDirIdx) {
                selectedDir = dir;
            }
            dirIdx++;
        }
        var fleePt = selectedDir.point;
        moveBy(fleePt.x, fleePt.y, fleePt.z);
    }

    @Override
    public InventoryItem[] drop() {
        return new InventoryItem[] { new Meat() };
    }


    public int getFov() {
        return 3;
    }
}
