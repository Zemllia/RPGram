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

    private ArrayList<Player> players = new ArrayList<>();

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
                    Player newPlayer = new Player(update.getMessage().getFrom().getFirstName(), pos, userId, map);
                    newPlayer.state = "";
                    newPlayer.worldState = "worldMap";
                    players.add(newPlayer);
                    if(newPlayer.worldState.equals("worldMap")) {
                        map.instantiateNewPlayer(newPlayer.getPos(), newPlayer.mapIcon, -1);
                    } else {
                        String[] worldStateSplited = newPlayer.worldState.split(" ");
                        if(worldStateSplited[0].equals("village")){
                            map.instantiateNewPlayer(newPlayer.getPos(), newPlayer.mapIcon, Integer.parseInt(worldStateSplited[1]));
                        }
                    }

                    System.out.println("Created new player: Name=" + newPlayer.name + " id=" + newPlayer.id +
                            " position=x" + newPlayer.getPos().x + ", y" + newPlayer.getPos().y);
                } else {
                    changePos(userId);
                }

                System.out.println(update.getMessage().getFrom().getFirstName() + ": " + update.getMessage().getText());

                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(executePlayerCommand(update.getMessage().getFrom().getId(),
                                        update.getMessage().getText())).enableHtml(true);
                

                message.setReplyMarkup(getKeyBoardOfArrows(getPlayer(userId)));

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }  else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            Player curPlayer = getPlayer(chat_id);

            switch (call_data) {
                case "go_up": {
                    String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x - 1, curPlayer.getPos().y), map);
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "go_right": {
                    String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x, curPlayer.getPos().y + 1), map);
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "go_left": {
                    String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x, curPlayer.getPos().y - 1), map);
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "go_down": {
                    String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x + 1, curPlayer.getPos().y), map);
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "inventory": {
                    changePos(curPlayer.id);
                    String answ = curPlayer.inventory();
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "map": {
                    changePos(curPlayer.id);
                    String answ = map.viewMapArea(curPlayer.getPos(), curPlayer.fieldOfView, getUserWorld(curPlayer));
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "getWood": {
                    changePos(curPlayer.id);
                    String answ = curPlayer.getResource("дерево");
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "getRock": {
                    changePos(curPlayer.id);
                    String answ = curPlayer.getResource("камень");
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "enter": {
                    changePos(curPlayer.id);
                    String answ = curPlayer.enterToVillage();
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);

                    break;
                }
                case "getDirt": {
                    changePos(curPlayer.id);
                    String answ = curPlayer.getResource("землю");
                    sendEditedMessage(update, (int) message_id, answ, curPlayer);
                    break;
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


    private Player getPlayer(long id) {
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

    private int getUserWorld(Player player) {
        if(player.worldState.equals("worldMap")) {
            return -1;
        } else {
            String[] worldStateSplited = player.worldState.split(" ");
            if(worldStateSplited[0].equals("village")){
                return Integer.parseInt(worldStateSplited[1]);
            }
        }
        return -1;
    }

    private void sendEditedMessage(Update update, int message_id, String answ, Player curPlayer){
        EditMessageText new_message = new EditMessageText()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setMessageId(message_id)
                .setText(answ)
                .enableHtml(true)
                .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getKeyBoardOfArrows(Player player){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineUp = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineMiddle = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineDown = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineFooter = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineUnderFooter = new ArrayList<>();
        rowInlineUp.add(new InlineKeyboardButton().setText("^").setCallbackData("go_up"));
        rowInlineMiddle.add(new InlineKeyboardButton().setText("<").setCallbackData("go_left"));
        rowInlineMiddle.add(new InlineKeyboardButton().setText(">").setCallbackData("go_right"));
        rowInlineDown.add(new InlineKeyboardButton().setText("v").setCallbackData("go_down"));
        rowInlineFooter.add(new InlineKeyboardButton().setText("Инвентарь").setCallbackData("inventory"));
        rowInlineFooter.add(new InlineKeyboardButton().setText("Карта").setCallbackData("map"));
        char curChar = map.getSymbolOnPosAndLayer(player.getPos(), 3);
        if(curChar == '^'){
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть дерево").setCallbackData("getWood"));
        } else if (curChar == 'o') {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть камень").setCallbackData("getRock"));
        } else if (curChar == 'V') {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Войти в деревню").setCallbackData("enter"));
        } else {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть землю").setCallbackData("getDirt"));
        }
        // Set the keyboard to the markup
        rowsInline.add(rowInlineUp);
        rowsInline.add(rowInlineMiddle);
        rowsInline.add(rowInlineDown);
        rowsInline.add(rowInlineFooter);
        rowsInline.add(rowInlineUnderFooter);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
