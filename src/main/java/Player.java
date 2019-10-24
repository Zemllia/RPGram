import core.GameObject;
import core.Position;
import core.utils.Random;
import items.InventoryItem;
import items.Rock;
import items.money;
import items.wood;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    int id;
    String name;

    Position oldPos;

    private int HP = 100;
    int fieldOfView = 5;

    String state;
    String worldState;

    private PathFinding pathFinding = new PathFinding();

    private List<InventoryItem> inventory = new ArrayList<InventoryItem>();

    char mapIcon = '@';

    long autoUpdateMessageId = -1;

    private String[] welcomeMessages = {" прибыл из космоса", " вылез из под земли", " наконец-то вышел из дома",
            " был добавлен в мир"};

    Player(String name, Position pos, int id){
        super("name", pos, 200, '@');
        oldPos = pos;
        this.name = name;
        this.id = id;
        mapIcon = name.charAt(0);
        changeEnergy(100);
        System.out.println(name + welcomeMessages[(int)(Math.random() * ((welcomeMessages.length)))]);
        inventory.add(new money(25));
        inventory.add(new money(35));
        inventory.add(new wood(25));
        inventory.add(new wood(25));
        inventory.add(new money(40));
        inventory.add(new wood(25));
        inventory.add(new wood(25));
    }

    void teleportPlayer(Position newPosition){
        position.x = newPosition.x;
        position.y = newPosition.y;
    }


    String executeCommand(String command, Map map) {

        String answer = "";
        String[] commandArray = command.split(" ");

        String[] randUnknownCommandPhrases = {": Оу, ума не приложу как это сделать, но я могу вот что: \n",
                ": Вобще без понятия как это сделать :( Но мы можем попробовать: \n",
                ": Я думаю, я должен научиться делать это, но пока я могу только: \n"};

        String[] commands = {"идти x y - Идти на глобальные координаты x y\n",
                "осмотреть (местность | себя) - увидеть карту в поле вашего зрения/узнать статы своего персонажа\n",
                "спать - восстановить энергию, если ее мало\n",
                "Добыть (дерево/камень) - добыть ресурс, если вы на нем стоите\n",
                "Зайти - зайти в деревню"
        };

        if (commandArray[0].toLowerCase().equals("идти")) {
            Position targetPosition = new Position(Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2]));
            ArrayList<Position> path = pathFinding.findPath(targetPosition, position, map);
            if (getEnergy() >= path.size()) {
                position = targetPosition;
                changeEnergy(path.size() * -1);
                answer = ": Моя позиция: x=" + position.x + ", y=" + position.y;
            } else {
                answer = ": Что-то мне подсказывает, что мне не хватит сил добраться так далеко...";
            }

        } else if (commandArray[0].toLowerCase().equals("осмотреть")){
            if (commandArray[1].toLowerCase().equals("себя")){
                answer = ": Меня зовут: " + name + "\n" +
                        "HP: " + HP + "\n" +
                        "Энергия: " + getEnergy() + "\n" +
                        "Радиус обзора: " + fieldOfView;
            } else if (commandArray[1].toLowerCase().equals("местность")){
                String[] worldStateArray = worldState.split(" ");
                if(worldState.equals("worldMap")) {
                    return map.viewMapArea(getPos(), fieldOfView, -1);
                } else if(worldStateArray[0].equals("village")){
                    return map.viewMapArea(getPos(), fieldOfView,  Integer.parseInt(worldStateArray[1]));
                }
            } else {
                answer = ": Меня окружает только тьма...";
            }

        } else if (command.toLowerCase().equals("зайти")) {
            String[] worldStateArray = worldState.split(" ");
            System.out.println("DEBUG: player position: x=" + this.position.x + " y=" + this.position.y);
            Village curVillage = map.checkVillage(this.position);
            if(worldStateArray[0].equals("worldMap") && curVillage != null){
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

        } else if (commandArray[0].toLowerCase().equals("спать")){
            if(getEnergy() <= 50) {
                changeEnergy(99);
                answer =  ": Z-z-z-z...";
            } else {
                answer = ": Я пока не устал!";
            }

        } else if (commandArray[0].toLowerCase().equals("тп")){
            teleportPlayer(new Position(Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2])));
            answer = ": Вы заюзали дебаг функцию, админы уже заметили это, за вами выехал магический спецназ!";

        } else if (commandArray[0].toLowerCase().equals("инвентарь")) {
            sortInventory();
            sortInventory();
            sortInventory();
            answer = ": Вот что в моем мешке:\n";
            int counter = 1;
            for(InventoryItem item : inventory){
                answer += counter + ") " + item.getName() + " x" + item.getCount()+ "\n";
                counter++;
            }
        } else if (commandArray[0].toLowerCase().equals("добыть")) {
            if(commandArray[1].toLowerCase().equals("дерево")){
                if(map.gameMap[3][position.x][position.y] == '^'){
                    int addedWood = Random.randInt(15, 30);
                    inventory.add(new wood(addedWood));
                    map.gameMap[3][position.x][position.y] = 0;
                    answer = ": Добыл немного дерева (x" + addedWood + ")";
                } else {
                    answer = ": Я не могу добыть то, чего нет";
                }
            } else if(commandArray[1].toLowerCase().equals("камень")){
                if(map.gameMap[3][position.x][position.y] == 'o'){
                    int addedRock = Random.randInt(5, 15);
                    inventory.add(new Rock(addedRock));
                    map.gameMap[3][position.x][position.y] = 0;
                    answer = ": Добыл немного камня (x" + addedRock + ")";
                } else {
                    answer = ": Я не могу добыть то, чего нет";
                }
            }
        } else {
            answer = randUnknownCommandPhrases[Random.randInt(0, randUnknownCommandPhrases.length - 1)];
            for (String item: commands) {
                answer += item;
            }
        }
        return answer;
    }

    void sortInventory(){
        for (int i = 0; i < inventory.size(); i++) {
            for (int j = i+1; j < inventory.size(); j++) {
                if (inventory.get(i).getName().equals(inventory.get(j).getName())){
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

    public String movePlayer(Position targetPos, Map map){
        String answer;
        ArrayList<Position> path = pathFinding.findPath(targetPos, position, map);
        if (getEnergy() >= path.size()) {
            oldPos=position;
            position = targetPos;
            changeEnergy(path.size() * -1);
            answer = ": Моя позиция: x=" + position.x + ", y=" + position.y;
        } else {
            answer = ": Что-то мне подсказывает, что мне не хватит сил добраться так далеко...";
        }
        return answer;
    }

    public long getAutoUpdateMessageId(){
        return autoUpdateMessageId;
    }

    public void setAutoUpdateMessageId(long id){
        autoUpdateMessageId = id;
    }
}
