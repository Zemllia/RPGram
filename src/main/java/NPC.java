import core.Position;
import items.Food;
import items.InventoryItem;
import items.Money;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NPC {
    private int id;
    private String name;
    private String race;

    enum State {
        CALM,
        HUNGRY,
        THIRSTY,
        NEED_MONEY,
        WORKING
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
    String curNeed = null;

    public NPC(int id, String name, Quest[] quests, Position position, int mapID) {
        this.id = id;
        this.name = name;
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
                if(hunger <= 200) {
                    state = State.HUNGRY;
                }
                break;

            case HUNGRY:
                hunger -= 1;
                thirst -= 2;
                fatigue -= 1;

                Food item = (Food) getIfExistsInInventoryByItemType("eatable");
                if(item != null){
                    hunger += item.getNutritionalValue();
                }

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
}
