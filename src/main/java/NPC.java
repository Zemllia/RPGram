import core.Position;
import core.utils.Random;
import items.InventoryItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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


    public NPC(int id, String name, Quest[] quests, Position position, int mapID) {
        this.id = id;
        this.name = name;
        this.mapID = mapID;
        this.quests = quests;
        this.position = position;
        if(this.quests != null){
            de = new DialogEngine(true);
        } else {
            de = new DialogEngine(false);
        }
    }

    public void makeAction(){

    }
}
