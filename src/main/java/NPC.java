import core.Position;
import items.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class NPC {
    private int id;
    private String name;
    private String race;

    // TODO Дописать про идеологию
    private String ideology;

    Position position;
    int mapID;

    private DialogEngine de;
    private Quest[] quests = null;

    private List<InventoryItem> inventory = new ArrayList<InventoryItem>();

    int HP = 100;
    int hunger = 100;
    int thirst = 100;
    int fatigue = 100;

    String state = "calm";
    String curNeed = null;

    ArrayList<String> needsQuery = new ArrayList<>();

    public NPC(int id, String name, Quest[] quests, Position position, int mapID) {
        this.id = id;
        this.name = name;
        this.mapID = mapID;
        this.quests = quests;
        this.position = position;
        de = new DialogEngine(quests, name);
    }

    public void makeAction(){
        if(state.equals("calm")){
            hunger -= 1;
            thirst -= 2;
            fatigue -= 1;
            if(needsQuery.size() != 0 && curNeed != null){
                curNeed = needsQuery.get(0);
                needsQuery.remove(0);
            }
            if (curNeed.equals("food")){

            }
        }
    }
}
