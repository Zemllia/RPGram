import core.Position;
import core.utils.Random;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

public class RPGramm extends TelegramLongPollingBot {

    RPGramm(DefaultBotOptions options) {
        super(options);
    }

    private ArrayList<Player> players = new ArrayList<Player>();

    private Map map = new Map(500, 500, 5, 5, 3);

    void init(){
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
                    newPlayer.state = "";
                    newPlayer.worldState = "worldMap";
                    players.add(newPlayer);
                    if(newPlayer.worldState == "worldMap") {
                        map.instantiateNewPlayer(newPlayer.getPos(), newPlayer.mapIcon, -1);
                    } else {
                        String[] worldStateSplited = newPlayer.worldState.split(" ");
                        if(worldStateSplited[0] == "village"){
                            map.instantiateNewPlayer(newPlayer.getPos(), newPlayer.mapIcon, Integer.parseInt(worldStateSplited[1]));
                        }
                    }

                    System.out.println("Created new player: Name=" + newPlayer.name + " id=" + newPlayer.id +
                            " position=x" + newPlayer.getPos().x + ", y" + newPlayer.getPos().x);
                } else {
                    changePos(userId);
                }

                System.out.println(update.getMessage().getFrom().getFirstName() + ": " + update.getMessage().getText());

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
                //TODO Работа с беседами
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

    private boolean checkIfExists(int id){
        for (Player item: players) {
            if(item.id == id){
                return true;
            }
        }
        return false;
    }

    private String executePlayerCommand(int id, String command){
        for (Player item: players) {
            if(item.id == id){
                return item.name + item.executeCommand(command, map);
            }
        }
        return null;
    }

    private void changePos(int id){
        for (Player item: players) {
            if(item.id == id){
                if(item.getPos().x > map.maxXBound){
                    item.teleportPlayer(new Position(item.getPos().x - map.maxXBound, item.getPos().y));
                }
                if(item.worldState.equals("worldMap")) {
                    map.changePlayerPos(item.oldPos, item.getPos(), item.mapIcon, -1);
                } else {
                    String[] worldStateSplited = item.worldState.split(" ");
                    if(worldStateSplited[0].equals("village")){
                        map.changePlayerPos(item.oldPos, item.getPos(), item.mapIcon, Integer.parseInt(worldStateSplited[1]));
                    }
                }
            }
        }
    }

    private boolean isUser(long id){
        return id > 0;
    }
}
