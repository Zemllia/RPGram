package rpgram.creatures;

import rpgram.core.I18N;
import rpgram.core.Position;
import rpgram.core.utils.Random;
import rpgram.items.*;
import rpgram.maps.*;

import java.util.ArrayList;

public class Player extends Creature {
    public PlayerState state;

    public Player(String name, Position pos, int id, BaseMap map) {
        super(id, name, map, name.charAt(0), MapWeight.HIGHEST, pos);
        changeEnergy(100);
        System.out.println(name + ": " + I18N.getRandom("player.welcome.{0}"));
        inventory.add(new Coin(1000));
    }

    public String enterVillage() {
        if (map instanceof VillageMap) {
            return I18N.get("map.cantEnter");
        }

        System.out.println("DEBUG: player position: x=" + this.position.x + " y=" + this.position.y);

        VillageMap curVillage = ((GlobalMap) map).checkVillage(this.position);
        if (curVillage == null) {
            return I18N.get("map.noVillage");
        }

        map = curVillage;
        lastPos = position;
        position = new Position(curVillage.getAreaWidth() / 2, curVillage.getAreaHeight() / 2);

        map.moveObject(this);
        return String.join(
            "\n",
            I18N.get("place.canRest"),
            I18N.get("place.info"),
            I18N.get("place.name") + ": " + curVillage.getName(),
            I18N.get("place.owner") + ": " + curVillage.getOwnerName(),
            I18N.get("place.population") + ": " + curVillage.getVillagersCount()
        );
    }

    public String exitVillage() {
        if (!(map instanceof VillageMap)) {
            return I18N.get("map.cantExit");
        }

        VillageMap curVillage = (VillageMap) map;

        map = curVillage.getParentMap();
        lastPos = position;
        position = new Position(curVillage.getPosition().x, curVillage.getPosition().y);

        map.moveObject(this);
        return I18N.get("map.adventureTime");
    }

    public <T extends InventoryItem> String getResource(Class<T> type) {
        String answer = null;
        // TODO: Remove duplicates (generify code)
        if (type.equals(Wood.class)) {
            if (map.getChar(MapLayer.ENVIRONMENT, position) == MapLegend.TREE.getValue()) {
                int addedWood = Random.randInt(15, 30);
                inventory.add(new Wood(addedWood));
                map.setChar(MapLayer.ENVIRONMENT, position, (char) 0);
                answer = I18N.get("player.gotSome") + " " + I18N.get("object.accusative.wood") + " (x" + addedWood + ")";
            } else {
                answer = I18N.get("player.cantGetNothing");
            }
        } else if (type.equals(Rock.class)) {
            if (map.getChar(MapLayer.ENVIRONMENT, position) == MapLegend.ROCK.getValue()) {
                int addedRock = Random.randInt(5, 15);
                inventory.add(new Rock(addedRock));
                map.setChar(MapLayer.ENVIRONMENT, position, (char) 0);
                answer = I18N.get("player.gotSome") + " " + I18N.get("object.accusative.rock") + " (x" + addedRock + ")";
            } else {
                answer = I18N.get("player.cantGetNothing");
            }
        } else if (type.equals(Dirt.class)) {
            if (map.getChar(MapLayer.ENVIRONMENT, position) == 0) {
                int addedDirt = Random.randInt(5, 15);
                inventory.add(new Dirt(addedDirt));
                map.setChar(MapLayer.ENVIRONMENT, position, MapLegend.HOLE.getValue());
                answer = I18N.get("player.gotSome") + " " + I18N.get("object.accusative.dirt") + " (x" + addedDirt + ")";
            } else {
                answer = I18N.get("player.cantGetNothing");
            }
        }
        return answer;
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

    @Override
    public String getStats() {
        StringBuilder answer = new StringBuilder(super.getStats());
        sortInventory();
        sortInventory();
        sortInventory();
        answer.append(I18N.get("player.whatsInMyBag")).append(":\n");
        int counter = 1;
        for (InventoryItem item : inventory) {
            answer.append(counter).append(") ").append(item.getName()).append(" x").append(item.getCount()).append("\n");
            counter++;
        }
        return answer.toString();
    }

    public String getStatsLine() {
        return "[" + getPos().x + ", " + getPos().y + "] "
            + "LVL: " + getLevel() + "/" + getXp() + " "
            + "HP: " + getHp() + " E: " + getEnergy();
    }

    public ArrayList<Integer> saySomethingToAll(ArrayList<Creature> players) {
        ArrayList<Integer> arrayToReturn = new ArrayList<>();
        if (state == PlayerState.TALKING) {
            for (Creature p : players) {
                if (Math.abs(position.x - p.getPos().x) <= getFov()
                    || Math.abs(position.y - p.getPos().y) <= getFov()) {
                    arrayToReturn.add(p.getId());
                }
            }
        }
        return arrayToReturn;
    }
}
