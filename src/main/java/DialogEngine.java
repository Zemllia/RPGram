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

    public DialogEngine(Quest[] quests, String characterName) {
        this.quests = quests;
        this.characterName = characterName;
    }

    public Object getDialog(String messageType) {
        JSONObject obj;
        JSONArray arr = null;
        try {
            obj = (JSONObject) new JSONParser().parse(new FileReader("resources/dialogs/" + characterName + "_dialogs.json"));
            switch (messageType) {
            case "hello":
                arr = (JSONArray) obj.get("hello_phrases");
                break;
            case "random":
                arr = (JSONArray) obj.get("random_phrases");
                break;
            case "by":
                arr = (JSONArray) obj.get("by_phrases");
                break;
            case "quest":
                if (quests != null) {
                    return quests[0];
                } else {
                    return null;
                }
            }
            return arr.get(Random.randInt(0, arr.size() - 1));
        } catch (IOException | ParseException ex) {
            // ignored
        }
        return null;
    }
}
