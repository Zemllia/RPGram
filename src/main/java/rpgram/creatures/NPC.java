package rpgram.creatures;

import rpgram.DialogEngine;
import rpgram.Quest;
import rpgram.core.GameObject;
import rpgram.core.Position;
import rpgram.items.Food;
import rpgram.items.InventoryItem;
import rpgram.maps.BaseMap;

import java.util.ArrayList;
import java.util.List;

public class NPC extends GameObject {
    private String race;

    // TODO Дописать про идеологию
    private String ideology;

    private DialogEngine de;
    private Quest[] quests;

    private List<InventoryItem> inventory = new ArrayList<InventoryItem>();

    int HP = 100;
    int hunger = 1000;
    int thirst = 1000;
    int fatigue = 1000;

    NPCState state = NPCState.CALM;

    public NPC(int id, String name, Quest[] quests, Position position, BaseMap map) {
        super(id, name, position, 200, map, '@', '9');
        this.quests = quests;
        de = new DialogEngine(quests, name);
    }

    public void makeAction() {
        switch (state) {
        case CALM:
            hunger -= 1;
            thirst -= 2;
            fatigue -= 1;

            state = NPCState.NEED_MONEY;

            if (hunger <= 200) {
                state = NPCState.HUNGRY;
            }
            if (thirst <= 300) {
                state = NPCState.THIRSTY;
            }
            break;

        case HUNGRY:
            hunger -= 1;
            thirst -= 2;
            fatigue -= 1;

            Food item = (Food) getIfExistsInInventoryByItemType("eatable");
            if (item != null) {
                hunger += item.getNutritionalValue();
                removeItemFromInventory(item);
                state = NPCState.CALM;
            } else {
                state = NPCState.BUY_FOOD;
            }

            break;

        case THIRSTY:
            hunger -= 1;
            thirst -= 2;
            fatigue -= 1;

            item = (Food) getIfExistsInInventoryByItemType("drinkable");
            if (item != null) {
                thirst += item.getNutritionalValue();
                removeItemFromInventory(item);
                state = NPCState.CALM;
            } else {
                state = NPCState.BUY_WATER;
            }
            break;

        case BUY_FOOD:
            hunger -= 1;
            thirst -= 2;
            fatigue -= 1;


            break;
        }
    }

    private InventoryItem getIfExistsInInventoryByItemType(String type) {
        for (InventoryItem item : inventory) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    private void removeItemFromInventory(InventoryItem item) {
        inventory.remove(item);
    }
}
