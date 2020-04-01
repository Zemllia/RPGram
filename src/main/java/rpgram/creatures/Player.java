package rpgram.creatures;

import rpgram.core.GameObject;
import rpgram.core.Position;
import rpgram.core.utils.Random;
import rpgram.items.*;
import rpgram.maps.*;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    private int maxHP = 100;
    private int HP = 100;
    public int fieldOfView = 10;
    private int XP = 0;
    private int requiredXP = 10;
    private int level = 1;
    private int skillPoint = 0;

    public PlayerState state;

    private PathFinding pathFinding = new PathFinding();

    public List<InventoryItem> inventory = new ArrayList<InventoryItem>();

    public Player(String name, Position pos, int id, BaseMap map) {
        super(id, name, pos, 200, map, name.charAt(0), '9');
        changeEnergy(100);
        String[] welcomeMessages = {
            "Прибыл из космоса",
            "Вылез из под земли",
            "Наконец-то вышел из дома",
            "Был добавлен в мир"
        };
        System.out.println(name + ": " + welcomeMessages[(int) (Math.random() * ((welcomeMessages.length)))]);
        inventory.add(new Money(1000));
    }

    public void updatePos() {
        if (getPos().x > map.getAreaWidth()) {
            teleportPlayer(new Position(getPos().x - map.getAreaWidth(), getPos().y));
        } else {
            teleportPlayer(getPos());
        }
    }

    void teleportPlayer(Position newPosition) {
        lastPos = position;
        position = new Position(newPosition.x, newPosition.y);
        map.moveObject(this);
    }

    public String executeCommand(String command) {
        String[] commandArray = command.toLowerCase().trim().split(" ");

        StringBuilder answer = new StringBuilder();
        if (commandArray[0].equals("тп")) {
            teleportPlayer(new Position(Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2])));
            answer.append("Вы заюзали дебаг функцию, админы уже заметили это, за вами выехал магический спецназ!");
        } else if (commandArray[0].equals("/start")) {
            answer.append("Привет от RPGram!");
        }
        return answer.toString();
    }

    void sortInventory() {
        for (int i = 0; i < inventory.size(); i++) {
            for (int j = i + 1; j < inventory.size(); j++) {
                if (inventory.get(i).getName().equals(inventory.get(j).getName())) {
                    inventory.get(i).increaseCount(inventory.get(j).getCount());
                    inventory.remove(j);
                }
            }
        }
    }

    public String movePlayer(Position targetPos) {
        ArrayList<Position> path = pathFinding.findPath(targetPos, position, map);
        if (getEnergy() >= path.size()) {
            teleportPlayer(targetPos);

            changeEnergy(path.size() * -1);
            increaseXP();
            return map.viewMapArea(position, fieldOfView);
        } else {
            return "Что-то мне подсказывает, что мне не хватит сил добраться так далеко...";
        }
    }

    public String enterToVillage() {
        if (map instanceof VillageMap) {
            return "Здесь некуда заходить";
        }

        System.out.println("DEBUG: player position: x=" + this.position.x + " y=" + this.position.y);

        VillageMap curVillage = ((GlobalMap) map).checkVillage(this.position);
        if (curVillage == null) {
            return "Тут деревни нет!";
        }

        map = curVillage;
        lastPos = position;
        position = new Position(curVillage.getAreaWidth() / 2, curVillage.getAreaHeight() / 2);

        map.moveObject(this);
        return "Хм, в поселении можно отдохнуть \n" +
            "Информация о поселении:\n" +
            "Название: " + curVillage.getName() + "\n" +
            "Владелец: " + curVillage.getOwnerName() + "\n" +
            "Население: " + curVillage.getVillagersCount();
    }

    public String exitFromVillage() {
        if (!(map instanceof VillageMap)) {
            return "Мне неоткуда выходить";
        }

        VillageMap curVillage = (VillageMap) map;

        map = curVillage.getParentMap();
        lastPos = position;
        position = new Position(curVillage.getPosition().x, curVillage.getPosition().y);

        map.moveObject(this);
        return "Пора продолжать приключения";
    }

    public String getResource(String whatToGet) {
        String answer = null;
        // TODO: Remove duplicates
        switch (whatToGet.toLowerCase()) {
        case "дерево":
            if (map.layer(MapLayers.ENVIRONMENT)[position.x][position.y] == '^') {
                int addedWood = Random.randInt(15, 30);
                inventory.add(new Wood(addedWood));
                map.layer(MapLayers.ENVIRONMENT)[position.x][position.y] = 0;
                answer = "Добыл немного дерева (x" + addedWood + ")";
            } else {
                answer = "Я не могу добыть то, чего нет";
            }
            break;
        case "камень":
            if (map.layer(MapLayers.ENVIRONMENT)[position.x][position.y] == '*') {
                int addedRock = Random.randInt(5, 15);
                inventory.add(new Rock(addedRock));
                map.layer(MapLayers.ENVIRONMENT)[position.x][position.y] = 0;
                answer = "Добыл немного камня (x" + addedRock + ")";
            } else {
                answer = "Я не могу добыть то, чего нет";
            }
            break;
        case "землю":
            if (map.layer(MapLayers.ENVIRONMENT)[position.x][position.y] == 0) {
                int addedDirt = Random.randInt(5, 15);
                inventory.add(new Dirt(addedDirt));
                map.layer(MapLayers.ENVIRONMENT)[position.x][position.y] = 'o';
                answer = "Добыл немного Земли (x" + addedDirt + ")";
            } else {
                answer = "Я не могу добыть то, чего нет";
            }
            break;
        }
        return answer;
    }

    public String inventory() {
        sortInventory();
        sortInventory();
        sortInventory();
        StringBuilder answer;
        answer = new StringBuilder("Здоровье: " + HP + "\n");
        answer.append("Максимум здоровья: ").append(maxHP).append("\n");
        answer.append("Радиус зрения: ").append(fieldOfView).append("\n");
        answer.append("XP: ").append(XP).append("\n");
        answer.append("Уровень: ").append(level).append("\n");
        answer.append("Количество очков уровня: ").append(skillPoint).append("\n");
        answer.append("Необходимо для следующего уровня: ").append(requiredXP).append("\n\n");
        answer.append("Вот что в моем мешке:\n");
        int counter = 1;
        for (InventoryItem item : inventory) {
            answer.append(counter).append(") ").append(item.getName()).append(" x").append(item.getCount()).append("\n");
            counter++;
        }
        return answer.toString();
    }

    public ArrayList<Integer> saySomethingToAll(ArrayList<Player> players) {
        ArrayList<Integer> arrayToReturn = new ArrayList<>();
        if (state == PlayerState.TALKING) {
            for (Player p : players) {
                if (Math.abs(position.x - p.position.x) <= fieldOfView
                    || Math.abs(position.y - p.position.y) <= fieldOfView) {
                    arrayToReturn.add(p.getId());
                }
            }
        }
        return arrayToReturn;
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

    private void increaseXP() {
        XP += 1;
        if (XP >= requiredXP) {
            levelUP();
        }
    }

    private String levelUP() {
        level = +1;
        skillPoint += 1;
        XP = 0;
        requiredXP = requiredXP + (int) ((float) requiredXP / 100 * 30);
        return "Уровень повышен";
    }

    public String increaseHP(int HPToIncrease) {
        if (skillPoint > 0) {
            maxHP += HPToIncrease;
            skillPoint -= 1;
            return ("Количество HP увеличено на " + HPToIncrease);
        } else {
            return ("У вас недостатачно очков опыта");
        }
    }

    public String increaseFOV(int fieldOfViewToIncrease) {
        if (skillPoint > 0) {
            fieldOfView += fieldOfViewToIncrease;
            skillPoint -= 1;
            return ("Поле зрения увеличено на " + fieldOfViewToIncrease);
        } else {
            return ("У вас недостатачно очков опыта");
        }
    }
}
