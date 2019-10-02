import core.Position;
import core.utils.Random;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

public class RPGramm extends TelegramLongPollingBot {

    public RPGramm(DefaultBotOptions options) {
        super(options);
    }

    public ArrayList<Player> players = new ArrayList<Player>();

    public Map map = new Map(500, 500, 5, 5, 3);

    public void init(){
        map.generateMap();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if(isUser(update.getMessage().getChatId())) {
                int userId = update.getMessage().getFrom().getId();
                if(!checkIfExists(userId)){
                    Position pos  = new Position(Random.randInt(0,500), Random.randInt(0,500));
                    while(map.gameMap[4][pos.x][pos.y] == '@'){
                        pos = new Position(Random.randInt(0,500), Random.randInt(0,500));
                    }
                    Player newPlayer = new Player(update.getMessage().getFrom().getFirstName(), pos, userId);
                    players.add(newPlayer);
                    map.instantiateNewPlayer(newPlayer.getPos(), newPlayer.mapIcon);
                } else {
                    changePos(userId);
                }


                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(executePlayerCommand(update.getMessage().getFrom().getId(),
                                        update.getMessage().getText())).enableHtml(true);


                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }

    @Override
    public String getBotUsername() {
        return "RPgram_bot";
    }

    @Override
    public String getBotToken() {
        return "658606256:AAG3O_p83oGSI8feIGLFadJFWzZY4rbch4c";
    }

    public boolean checkIfExists(int id){
        for (Player item: players) {
            System.out.println(item.id);
            if(item.id == id){
                return true;
            }
        }
        return false;
    }

    String executePlayerCommand(int id, String command){
        for (Player item: players) {
            if(item.id == id){
                String[] commandArray = command.split(" ");
                if(commandArray[0].toLowerCase().equals("осмотреть") && commandArray[1].toLowerCase().equals("местность")){
                    System.out.println("Trying to look at map");
                    return map.viewMapArea(item.getPos(), item.fieldOfView);
                }
                String answer = item.name + item.executeCommand(command);
                return answer;
            }
        }
        return "Шото непонятно...";
    }

    void changePos(int id){
        for (Player item: players) {
            if(item.id == id){
                map.changePlayerPos(item.oldPos, item.getPos(), item.mapIcon);
            }
        }
    }

    public boolean isUser(long id){
        System.out.println(id);
        return id > 0;
    }
}

