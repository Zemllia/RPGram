import core.utils.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DialogEngine {

    private boolean haveQuests;
    private String characterName;

    public DialogEngine(boolean haveQuests, String characterName){
        this.haveQuests = haveQuests;
        this.characterName = characterName;
    }

    public String getDialog(){
        JSONObject obj;
        JSONArray arr;
        try {
            obj = (JSONObject) new JSONParser().parse(new FileReader("resources/dialogs/"+ characterName + "_dialogs.json"));
            arr = (JSONArray) obj.get("hello_phrases");
            return (String) arr.get(Random.randInt(0, arr.size() - 1));
        } catch (FileNotFoundException ex) {

        } catch (IOException | ParseException ex) {
        }
        return null;
    }
}
