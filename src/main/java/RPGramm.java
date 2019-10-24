import core.Position;
import core.utils.Random;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

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

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
                List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
                rowInline.add(new InlineKeyboardButton().setText("Автоматическое обновление карты").setCallbackData("update_msg_text"));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                message.setReplyMarkup(markupInline);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                //TODO Работа с беседами
            }
        }  else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            int chat_id = update.getCallbackQuery().getMessage().getFrom().getId();
            Player curPlayer = getPlayer(chat_id);
            int playerWorld = getUserWorld(curPlayer);

            String answ = map.viewMapArea (curPlayer.getPos(), curPlayer.fieldOfView, playerWorld);

            if (call_data.equals("update_msg_text")) {
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true);
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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


    public Player getPlayer (int id) {
        for (Player item: players) {
            if(item.id == id){
               return item;
            }
        }
        return players.get(0);
    }

    private boolean isUser(long id){
        return id > 0;
    }

    int getUserWorld (Player player) {
        if(player.worldState == "worldMap") {
            return -1;
        } else {
            String[] worldStateSplited = player.worldState.split(" ");
            if(worldStateSplited[0] == "village"){
                return Integer.parseInt(worldStateSplited[1]);
            }
        }
        return -1;
    }
}
