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
            } else {
                //TODO Работа с беседами
            }
        }  else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            Player curPlayer = getPlayer(chat_id);
            int playerWorld = getUserWorld(curPlayer);

            if (call_data.equals("go_up")) {
                curPlayer.movePlayer(new Position(curPlayer.getPos().x - 1, curPlayer.getPos().y), map);
                changePos(curPlayer.id);
                System.out.println(curPlayer.name);
                String answ = map.viewMapArea (curPlayer.getPos(), curPlayer.fieldOfView, playerWorld);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("go_right")) {
                curPlayer.movePlayer(new Position(curPlayer.getPos().x, curPlayer.getPos().y + 1), map);
                changePos(curPlayer.id);
                String answ = map.viewMapArea (curPlayer.getPos(), curPlayer.fieldOfView, playerWorld);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("go_left")) {
                curPlayer.movePlayer(new Position(curPlayer.getPos().x, curPlayer.getPos().y - 1), map);
                changePos(curPlayer.id);
                String answ = map.viewMapArea (curPlayer.getPos(), curPlayer.fieldOfView, playerWorld);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("go_down")) {
                curPlayer.movePlayer(new Position(curPlayer.getPos().x + 1, curPlayer.getPos().y), map);
                changePos(curPlayer.id);
                String answ = map.viewMapArea (curPlayer.getPos(), curPlayer.fieldOfView, playerWorld);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("inventory")) {
                changePos(curPlayer.id);
                String answ = curPlayer.executeCommand("инвентарь", map);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("map")) {
                changePos(curPlayer.id);
                String answ = curPlayer.executeCommand("осмотреть местность", map);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("getWood")) {
                changePos(curPlayer.id);
                String answ = curPlayer.executeCommand("добыть дерево", map);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("getRock")) {
                changePos(curPlayer.id);
                String answ = curPlayer.executeCommand("добыть камень", map);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("enter")) {
                changePos(curPlayer.id);
                String answ = curPlayer.executeCommand("зайти", map);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("getDirt")) {
                changePos(curPlayer.id);
                String answ = curPlayer.executeCommand("добыть землю", map);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setMessageId((int) message_id)
                        .setText(answ)
                        .enableHtml(true)
                        .setReplyMarkup(getKeyBoardOfArrows(curPlayer));
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


    public Player getPlayer (long id) {
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

    InlineKeyboardMarkup getKeyBoardOfArrows(Player player){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInlineUp = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> rowInlineMiddle = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> rowInlineDown = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> rowInlineFooter = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> rowInlineUnderFooter = new ArrayList<InlineKeyboardButton>();
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
