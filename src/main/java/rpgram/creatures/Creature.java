package rpgram.creatures;

import com.crown.creatures.Organism;
import com.crown.i18n.I18n;
import com.crown.i18n.ITemplate;
import com.crown.items.InventoryItem;
import com.crown.maps.*;
import rpgram.ui.MapIcons;

public abstract class Creature extends Organism {
    public Creature(
        String name,
        Map map,
        MapIcon<?> mapIcon,
        MapWeight mapWeight,
        Point3D position,
        int maxEnergy,
        int maxHp,
        int level,
        int speed
    ) {
        super(
            name,
            map,
            mapIcon,
            mapWeight,
            position,
            maxEnergy,
            maxHp,
            level,
            speed
        );
    }

    public ITemplate getStats() {
        return I18n.fmtOf(
            String.join(
                "\n",
                "{0}",
                "{1}: {2}/{3}",
                "{4}: {5}/{6}",
                "{7}: {8}/{9}",
                "{10}: {11}",
                "{12}: {13}",
                "{14}: {15}",
                "{16}: {17}",
                "@ {18}"
            ),
            getKeyName(),
            "stats.hp", getHp(), getMaxHp(),
            "stats.energy", getEnergy(), getMaxEnergy(),
            "stats.speed", getSpeed(), getMaxSpeed(),
            "stats.level", getLevel(),
            "stats.xp", getXp(),
            "stats.xp.toNextLevel", getXpForLevel(level + 1),
            "stats.skillPoints", getSkillPoints(),
            getPt0()
        );
    }

    @Override
    public MapIcon<?> getMapIcon() {
        return MapIcons.getIcons().get(getMapIconId());
    }

    @Override
    public InventoryItem[] drop() {
        return getInventory().toArray(new InventoryItem[0]);
    }
}
