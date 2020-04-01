import core.GameObject;
import core.Position;
import core.utils.Random;
import items.*;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    int id;
    String name;

    Position oldPos;

    private int maxHP = 100;
    private int HP = 100;
    int fieldOfView = 5;
    private int XP = 0;
    private int requiredXP = 10;
    private int level = 1;
    private int skillPoint = 0;

    String state;
    String worldState;

    private PathFinding pathFinding = new PathFinding();

    public List<InventoryItem> inventory = new ArrayList<InventoryItem>();

    char mapIcon;

    Map map;

    Player(String name, Position pos, int id, Map map) {
        super("name", pos, 200, '@');
        oldPos = pos;
        this.name = name;
        this.id = id;
        mapIcon = name.charAt(0);
        changeEnergy(100);
        this.map = map;
        String[] welcomeMessages = { " прибыл из космоса", " вылез из под земли", " наконец-то вышел из дома",
            " был добавлен в мир" };
        System.out.println(name + welcomeMessages[(int) (Math.random() * ((welcomeMessages.length)))]);
        inventory.add(new Money(1000));
    }

    void teleportPlayer(Position newPosition) {
        position.x = newPosition.x;
        position.y = newPosition.y;
    }


    String executeCommand(String command, Map map) {
        String[] commandArray = command.split(" ");

        String[] randUnknownCommandPhrases = { ": Оу, ума не приложу как это сделать, но я могу вот что: \n",
            ": Вобще без понятия как это сделать :( Но мы можем попробовать: \n",
            ": Я думаю, я должен научиться делать это, но пока я могу только: \n" };

        String[] commands = { "идти x y - Идти на глобальные координаты x y\n",
            "осмотреть (местность | себя) - увидеть карту в поле вашего зрения/узнать статы своего персонажа\n",
            "спать - восстановить энергию, если ее мало\n",
            "Добыть (дерево/камень) - добыть ресурс, если вы на нем стоите\n",
            "Зайти - зайти в деревню"
        };

        StringBuilder answer;
        if (commandArray[0].toLowerCase().equals("тп")) {
            teleportPlayer(new Position(Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2])));
            answer = new StringBuilder(": Вы заюзали дебаг функцию, админы уже заметили это, за вами выехал магический спецназ!");
        } else {
            answer = new StringBuilder(randUnknownCommandPhrases[Random.randInt(0, randUnknownCommandPhrases.length - 1)]);
            for (String item : commands) {
                answer.append(item);
            }
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

    public String getName() {
        return name;
    }

    public Position getPos() {
        return position;
    }

    public String movePlayer(Position targetPos, Map map) {
        String answer;
        ArrayList<Position> path = pathFinding.findPath(targetPos, position, map);
        if (getEnergy() >= path.size()) {
            oldPos = position;
            position = targetPos;
            changeEnergy(path.size() * -1);
            increaseXP();
            if (worldState.equals("worldMap")) {
                map.changePlayerPos(oldPos, position, mapIcon, -1);
            } else {
                String[] worldStateSplited = worldState.split(" ");
                if (worldStateSplited[0].equals("village")) {
                    map.changePlayerPos(oldPos, position, mapIcon, Integer.parseInt(worldStateSplited[1]));
                }
            }
            answer = map.viewMapArea(position, fieldOfView, getUserWorld());
        } else {
            answer = ": Что-то мне подсказывает, что мне не хватит сил добраться так далеко...";
        }
        return answer;
    }

    public String enterToVillage() {
        String answer;
        String[] worldStateArray = worldState.split(" ");
        System.out.println("DEBUG: player position: x=" + this.position.x + " y=" + this.position.y);
        Village curVillage = map.checkVillage(this.position);
        if (worldStateArray[0].equals("worldMap") && curVillage != null) {
            worldState = "village " + curVillage.getVillageID();
            position = new Position(25, 25);
            oldPos = position;
            answer = ": Хм, в поселении можно отдохнуть \n" +
                "Информация о поселении:\n" +
                "Название: " + curVillage.getVillageName() + "\n" +
                "Владелец: " + curVillage.getOwnerName() + "\n" +
                "Население: " + curVillage.getVillagersCount();
            map.instantiateNewPlayer(position, mapIcon, curVillage.getVillageID());

        } else {
            answer = ": Здесь некуда заходить";
        }
        return answer;
    }

    public String exitFromVillage() {
        String answer;
        String[] worldStateSplitted = worldState.split(" ");
        if (worldStateSplitted[0].equals("village")) {
            Village curVillage = map.villages.get(Integer.parseInt(worldStateSplitted[1]));
            worldState = "worldMap";
            position = new Position(curVillage.villagePos.x, curVillage.villagePos.y);
            oldPos = position;
            answer = "Пора продолжать приключения";
            map.instantiateNewPlayer(position, mapIcon, -1);
        } else {
            answer = "Мне неоткуда выходить";
        }
        return answer;
    }

    public String getResource(String whatToGet) {
        String answer = null;
        if (whatToGet.toLowerCase().equals("дерево")) {
            if (map.gameMap[3][position.x][position.y] == '^') {
                int addedWood = Random.randInt(15, 30);
                inventory.add(new Wood(addedWood));
                map.gameMap[3][position.x][position.y] = 0;
                answer = "Добыл немного дерева (x" + addedWood + ")";
            } else {
                answer = "Я не могу добыть то, чего нет";
            }
        } else if (whatToGet.toLowerCase().equals("камень")) {
            if (map.gameMap[3][position.x][position.y] == '*') {
                int addedRock = Random.randInt(5, 15);
                inventory.add(new Rock(addedRock));
                map.gameMap[3][position.x][position.y] = 0;
                answer = "Добыл немного камня (x" + addedRock + ")";
            } else {
                answer = "Я не могу добыть то, чего нет";
            }
        } else if (whatToGet.toLowerCase().equals("землю")) {
            if (map.gameMap[3][position.x][position.y] == 0) {
                int addedDirt = Random.randInt(5, 15);
                inventory.add(new Dirt(addedDirt));
                map.gameMap[3][position.x][position.y] = 'o';
                answer = "Добыл немного Земли (x" + addedDirt + ")";
            } else {
                answer = "Я не могу добыть то, чего нет";
            }
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
        if (state.equals("talking")) {
            for (Player p : players) {
                if (Math.abs(position.x - p.position.x) <= fieldOfView
                    || Math.abs(position.y - p.position.y) <= fieldOfView) {
                    arrayToReturn.add(p.id);
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

    int getUserWorld() {
        if (worldState.equals("worldMap")) {
            return -1;
        } else {
            String[] worldStateSplited = worldState.split(" ");
            if (worldStateSplited[0].equals("village")) {
                return Integer.parseInt(worldStateSplited[1]);
            }
        }
        return -1;
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
