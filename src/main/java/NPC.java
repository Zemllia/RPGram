import core.GameObject;
import core.Position;
import items.Food;
import items.InventoryItem;
import items.Money;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NPC extends GameObject {
    private int id;
    private String race;

    enum State {
        CALM,
        HUNGRY,
        THIRSTY,
        TIRED,
        NEED_MONEY,
        WORKING,
        BUY_FOOD,
        BUY_WATER
    }

    // TODO Дописать про идеологию
    private String ideology;

    Position position;
    int mapID;

    private DialogEngine de;
    private Quest[] quests = null;

    private List<InventoryItem> inventory = new ArrayList<InventoryItem>();

    int HP = 100;
    int hunger = 1000;
    int thirst = 1000;
    int fatigue = 1000;

    State state = State.CALM;

    public NPC(int id, String name, Quest[] quests, Position position, int mapID) {
        super(name, new Position(0,0), 200, '@');
        this.id = id;
        this.mapID = mapID;
        this.quests = quests;
        this.position = position;
        de = new DialogEngine(quests, name);
    }

    public void makeAction(){
        switch (state) {
            case CALM:
                hunger -= 1;
                thirst -= 2;
                fatigue -= 1;

                state = State.NEED_MONEY;

                if(hunger <= 200) {
                    state = State.HUNGRY;
                }
                if(thirst <= 300){
                    state = State.THIRSTY;
                }
                break;

            case HUNGRY:
                hunger -= 1;
                thirst -= 2;
                fatigue -= 1;

                Food item = (Food) getIfExistsInInventoryByItemType("eatable");
                if(item != null){
                    hunger += item.getNutritionalValue();
                    removeItemFromInventory(item);
                    state = State.CALM;
                }
                else {
                    state = State.BUY_FOOD;
                }

                break;

            case THIRSTY:
                hunger -= 1;
                thirst -= 2;
                fatigue -= 1;

                item = (Food) getIfExistsInInventoryByItemType("drinkable");
                if(item != null){
                    thirst += item.getNutritionalValue();
                    removeItemFromInventory(item);
                    state = State.CALM;
                }
                else {
                    state = State.BUY_WATER;
                }
                break;

            case BUY_FOOD:
                hunger -= 1;
                thirst -= 2;
                fatigue -= 1;



                break;
        }
    }

    private InventoryItem getIfExistsInInventoryByItemType(String type){
        for(InventoryItem item : inventory){
            if(item.getType().equals(type)){
                return item;
            }
        }
        return null;
    }

    private void removeItemFromInventory(InventoryItem item){
        inventory.remove(item);
    }
}
