import core.utils.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DialogEngine {

    private Quest[] quests;
    private String characterName;

    public DialogEngine(Quest[] quests, String characterName){
        this.quests = quests;
        this.characterName = characterName;
    }

    public Object getDialog(String messageType){
        JSONObject obj;
        JSONArray arr = null;
        try {
            obj = (JSONObject) new JSONParser().parse(new FileReader("resources/dialogs/"+ characterName + "_dialogs.json"));
            if(messageType == "hello") {
                arr = (JSONArray) obj.get("hello_phrases");
            } else if (messageType == "random"){
                arr = (JSONArray) obj.get("random_phrases");
            } else if (messageType == "by"){
                arr = (JSONArray) obj.get("by_phrases");
            } else if (messageType == "quest"){
                if(quests != null){
                    return quests[0];
                } else {
                    return null;
                }
            }
            return arr.get(Random.randInt(0, arr.size() - 1));
        } catch (FileNotFoundException ex) {

        } catch (IOException | ParseException ex) {
        }
        return null;
    }
}
