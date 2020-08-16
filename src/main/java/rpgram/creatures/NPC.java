package rpgram.creatures;

import rpgram.core.utils.Random;
import rpgram.items.*;
import rpgram.maps.BaseMap;

import java.util.ArrayList;
import java.util.List;

public class NPC extends Creature {
    private DialogEngine de;
    private Quest[] quests;

    private List<InventoryItem> inventory = new ArrayList<>();

    private int satiety = 400;
    private int water = 400;

    NPCState state = NPCState.CALM;

    public NPC(int id, String name, Quest[] quests, Position position, BaseMap map) {
        super(id, name, map, '@', '9', position);
        this.quests = quests;
        de = new DialogEngine(quests, name);
        live();
    }

    public void live() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // ignored
                }
                doAction();
            }
        }).start();
    }

    public void doAction() {
        changeSatiety(-1);
        changeWater(-2);
        changeEnergy(-1);
        System.out.println(
            getName() + ": x=" + getPos().x
                + " y=" + getPos().y
                + " satiety=" + satiety
                + " water=" + water
                + " energy=" + getEnergy()
        );

        switch (state) {
        case CALM: {
            move(new Position(getPos().x + getRandomStep(), getPos().y + getRandomStep()));
            if (satiety <= 200) {
                state = NPCState.HUNGRY;
            } else if (water <= 300) {
                state = NPCState.THIRSTY;
            } else if (getEnergy() <= 0) {
                state = NPCState.TIRED;
            }
            break;
        }
        case HUNGRY: {
            Food item = tryGetFromInventory(Food.class);
            if (item == null || item.getType() != ItemType.MEAL) {
                state = NPCState.BUY_FOOD;
            } else {
                satiety += item.getNutritionalValue();
                inventory.remove(item);
                state = NPCState.CALM;
            }
            break;
        }
        case THIRSTY: {
            Food item = tryGetFromInventory(Food.class);
            if (item == null || item.getType() != ItemType.DRINK) {
                state = NPCState.BUY_WATER;
            } else {
                water += item.getNutritionalValue();
                inventory.remove(item);
                state = NPCState.CALM;
            }
            break;
        }
        case TIRED: {
            sleep();
            state = NPCState.CALM;
            break;
        }
        case WORKING: {
            if (hasEnoughMoney()) {
                state = NPCState.CALM;
            } else {
                inventory.add(new Coin(5));
            }
            break;
        }
        case BUY_FOOD: {
            if (hasEnoughMoney()) {
                Coin item = tryGetFromInventory(Coin.class);
                inventory.add(new RoastedMeat(10));
                inventory.remove(item);
            } else {
                state = NPCState.WORKING;
            }
            break;
        }
        case BUY_WATER: {
            if (hasEnoughMoney()) {
                Coin item = tryGetFromInventory(Coin.class);
                inventory.add(new RoastedMeat(10));
                inventory.remove(item);
            } else {
                state = NPCState.WORKING;
            }
        }
        }
    }

    public int getRandomStep() {
        return Random.rnd.nextBoolean() ? (Random.rnd.nextBoolean() ? 1 : -1) : 0;
    }

    public boolean hasEnoughMoney() {
        Coin item = tryGetFromInventory(Coin.class);
        return item != null && item.getCount() > 100;
    }

    public void changeSatiety(int delta) {
        satiety += delta;
    }

    public void changeWater(int delta) {
        water += delta;
    }

    private <T extends InventoryItem> T tryGetFromInventory(Class<T> cls) {
        for (InventoryItem item : inventory) {
            if (cls.isInstance(item)) {
                // noinspection unchecked
                return (T) item;
            }
        }
        return null;
    }
}
