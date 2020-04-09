package rpgram.creatures;

import rpgram.core.GameObject;
import rpgram.core.I18N;
import rpgram.core.Position;
import rpgram.items.InventoryItem;
import rpgram.maps.BaseMap;

import java.util.ArrayList;
import java.util.List;

public class Creature extends GameObject {
    private int maxHp;
    private int hp;

    private int maxEnergy;
    private int energy;

    private final int maxFov = 100;
    private int fov;

    private int level;
    private int xp = 0;
    private int xpToNextLevel = 10;
    private int skillPoints = 0;

    public List<InventoryItem> inventory = new ArrayList<>();

    public Creature(
        String name,
        BaseMap map,
        char mapIcon,
        char mapWeight,
        Position position
    ) {
        // RULE: default creature parameters
        this(
            name,
            map,
            mapIcon,
            mapWeight,
            position,
            200,
            100,
            1,
            5
        );
    }

    public Creature(
        String name,
        BaseMap map,
        char mapIcon,
        char mapWeight,
        Position position,
        int maxEnergy,
        int maxHp,
        int level,
        int fieldOfView
    ) {
        super(name, map, mapIcon, mapWeight, position);

        this.maxHp = hp = maxHp;
        this.maxEnergy = maxEnergy;
        // RULE: every creature appears with half energy
        this.energy = maxEnergy / 2;
        this.fov = fieldOfView;
        this.level = level;
    }

    public String getStats() {
        return String.join(
            "\n",
            I18N.get("stats.hp") + ": " + getHp() + "/" + getMaxHp(),
            I18N.get("stats.energy") + ": " + getEnergy() + "/" + getMaxEnergy(),
            I18N.get("stats.fov") + ": " + getFov(),
            I18N.get("stats.xp") + ": " + getXp(),
            I18N.get("stats.level") + ": " + getLevel(),
            I18N.get("stats.skillPoints") + ": " + getSkillPoints(),
            I18N.get("stats.xp.toNextLevel") + ": " + getXpToNextLevel()
        ) + "\n\n";
    }

    // region HP

    public int getMaxHp() {
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    public String adjustHp(int delta) {
        assert delta > 0;
        if (skillPoints > 0) {
            if (hp + delta > maxHp) {
                hp = maxHp;
                return I18N.get("stats.hp.max");
            }
            skillPoints -= 1;
            return changeHp(delta);
        } else {
            return I18N.get("stats.xp.notEnough");
        }
    }

    private String changeHp(int delta) {
        maxHp += delta;
        return I18N.getChangeable("stats.hp.{0}", delta);
    }

    // endregion

    // region Energy

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getEnergy() {
        return energy;
    }

    public String sleep() {
        return sleep(maxEnergy - energy);
    }

    public String sleep(int delta) {
        assert delta > 0 && energy + delta <= maxEnergy;
        if (energy <= 50) {
            return changeEnergy(delta);
        } else {
            return I18N.get("stats.energy.highEnough");
        }
    }

    protected String changeEnergy(int delta) {
        energy += delta;
        return I18N.getChangeable("stats.energy.{0}", delta);
    }

    // endregion

    // region FOV

    public int getMaxFov() {
        return maxFov;
    }

    public int getFov() {
        return fov;
    }

    public String adjustFov(int delta) {
        assert delta > 0;
        if (skillPoints > 0) {
            if (fov + delta > maxFov) {
                fov = maxFov;
                return I18N.get("stats.fov.max");
            }
            skillPoints -= 1;
            return changeFov(delta);
        } else {
            return I18N.get("stats.xp.notEnough");
        }
    }

    private String changeFov(int delta) {
        fov += delta;
        return I18N.getChangeable("stats.fov.{0}", delta);
    }

    // endregion

    // region XP

    public int getXp() {
        return xp;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    public String adjustXp() {
        return changeXp(1);
    }

    private String changeXp(int delta) {
        assert delta > 0;
        xp += delta;
        if (xp >= xpToNextLevel) {
            adjustLevel();
        }
        return I18N.get("stats.xp.increased", delta);
    }

    // endregion

    // region Level

    public int getLevel() {
        return level;
    }

    public String adjustLevel() {
        level += 1;
        skillPoints += 1;
        xp = 0;
        xpToNextLevel = xpToNextLevel + (int) ((float) xpToNextLevel / 100 * 30);
        return I18N.get("stats.level.next");
    }

    // endregion

    public int getSkillPoints() {
        return skillPoints;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    public void teleport(Position newPosition) {
        lastPos = position;
        position = newPosition;
        map.moveObject(this);
    }

    public String move(Position targetPos) {
        // TODO insert path finder
        if (getEnergy() >= 1) {
            teleport(targetPos);
            changeEnergy(-1);
            adjustXp();
            return map.viewMapArea(position, getFov());
        } else {
            return I18N.get("stats.energy.low");
        }
    }
}
