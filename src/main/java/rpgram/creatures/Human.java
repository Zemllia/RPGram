package rpgram.creatures;

import com.crown.creatures.Organism;
import com.crown.i18n.I18n;
import com.crown.i18n.ITemplate;
import com.crown.maps.*;
import com.crown.time.Action;
import rpgram.ui.MapIcons;

public class Human extends Organism {
    public String lang = "en";
    public final long telegramId;

    protected final int maxFov = 20;
    protected int fov;

    public Human(
        long telegramId,
        String name,
        Map map,
        MapIcon<?> mapIcon,
        MapWeight mapWeight,
        Point3D position
    ) {
        super(
            name,
            map,
            mapIcon,
            mapWeight,
            position,
            100,
            100,
            1,
            1
        );
        this.telegramId = telegramId;
        xp = 100;
        fov = 5;
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
                "{18}: {19}",
                "@ {20}"
            ),
            getKeyName(),
            "stats.hp", getHp(), getMaxHp(),
            "stats.energy", getEnergy(), getMaxEnergy(),
            "stats.speed", getSpeed(), getMaxSpeed(),
            "stats.fov", getFov(),
            "stats.level", getLevel(),
            "stats.xp", getXp(),
            "stats.xp.toNextLevel", getXpForLevel(level + 1),
            "stats.skillPoints", getSkillPoints(),
            getPt0()
        );
    }

    // region FOV

    /**
     * Maximal allowed field of vision for creature.
     */
    public int getMaxFov() {
        return maxFov;
    }

    /**
     * Returns creature's field of vision.
     */
    public int getFov() {
        return fov;
    }

    /**
     * Changes creature's field of vision by {@code delta}.
     * Timeline support included.
     */
    public ITemplate changeFovBy(int delta) {
        return getTimeline().perform(Action.change(this, "changeFov", delta));
    }

    /**
     * Internal logic, may be overridden if needed.
     */
    public ITemplate changeFov(int delta) {
        if (invalidDelta(fov, delta, maxFov)) {
            return I18n.invalidDeltaMessage;
        }
        fov += delta;
        return I18n.okMessage;
    }

    // endregion

    @Override
    public MapIcon<?> getMapIcon() {
        return MapIcons.getIcons().get(getMapIconId());
    }
}
