package rpgram.creatures;

import rpgram.core.GameObject;
import rpgram.core.Position;
import rpgram.items.InventoryItem;
import rpgram.maps.BaseMap;
import rpgram.maps.PathFinding;

import java.util.ArrayList;
import java.util.List;

public class Creature extends GameObject {
    private PathFinding pathFinding = new PathFinding();

    private int maxHP;
    private int HP;

    private int maxEnergy;
    private int energy;

    private int level;
    private int XP = 0;
    private int requiredXP = 10;
    private int skillPoint = 0;

    private int fieldOfView;

    public List<InventoryItem> inventory = new ArrayList<>();

    public Creature(int id, String name, BaseMap map, char mapIcon, char mapWeight, Position position) {
        // RULE: default creature parameters
        this(id, name, map, mapIcon, mapWeight, position, 200, 100, 1, 5);
    }

    public Creature(int id, String name, BaseMap map, char mapIcon, char mapWeight, Position position, int maxEnergy, int maxHP, int level, int fieldOfView) {
        super(id, name, map, mapIcon, mapWeight, position);

        this.maxHP = HP = maxHP;
        this.maxEnergy = maxEnergy;
        // RULE: every creature appears with half energy
        this.energy = maxEnergy / 2;
        this.fieldOfView = fieldOfView;
        this.level = level;
    }

    public String getStats() {
        return "Здоровье: " + getHP() + "\n" + "Максимум здоровья: " + getMaxHP() + "\n" +
            "Радиус зрения: " + getFieldOfView() + "\n" +
            "XP: " + getXP() + "\n" +
            "Уровень: " + getLevel() + "\n" +
            "Количество очков уровня: " + getSkillPoint() + "\n" +
            "Необходимо для следующего уровня: " + getRequiredXP() + "\n\n";
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getHP() {
        return HP;
    }

    public String increaseHP(int delta) {
        if (skillPoint > 0) {
            maxHP += delta;
            skillPoint -= 1;
            return "Количество HP увеличено на " + delta;
        } else {
            return "У вас недостатачно очков опыта";
        }
    }

    public int getFieldOfView() {
        return fieldOfView;
    }

    public String increaseFOV(int delta) {
        if (skillPoint > 0) {
            fieldOfView += delta;
            skillPoint -= 1;
            return "Поле зрения увеличено на " + delta;
        } else {
            return "У вас недостатачно очков опыта";
        }
    }

    public int getMaxEnergy() {
        return this.maxEnergy;
    }

    public int getEnergy() {
        return this.energy;
    }

    public void changeEnergy(int delta) {
        energy += delta;
    }

    public int getXP() {
        return XP;
    }

    public int getRequiredXP() {
        return requiredXP;
    }

    protected void increaseXP() {
        XP += 1;
        if (XP >= requiredXP) {
            increaseLevel();
        }
    }

    public int getLevel() {
        return level;
    }

    public String increaseLevel() {
        level += 1;
        skillPoint += 1;
        XP = 0;
        requiredXP = requiredXP + (int) ((float) requiredXP / 100 * 30);
        return "Уровень повышен";
    }

    public int getSkillPoint() {
        return skillPoint;
    }

    public String sleep() {
        String answer;
        if (getEnergy() <= 50) {
            changeEnergy(99);
            answer = "Z-z-z-z...";
        } else {
            answer = "Я пока не устал!";
        }
        return answer;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    public void teleport(Position newPosition) {
        lastPos = position;
        position = new Position(newPosition.x, newPosition.y);
        map.moveObject(this);
    }

    public String move(Position targetPos) {
        ArrayList<Position> path = pathFinding.findPath(targetPos, position, map);
        if (getEnergy() >= path.size()) {
            teleport(targetPos);

            changeEnergy(path.size() * -1);
            increaseXP();
            return map.viewMapArea(position, getFieldOfView());
        } else {
            return "Что-то мне подсказывает, что мне не хватит сил добраться так далеко...";
        }
    }
}
