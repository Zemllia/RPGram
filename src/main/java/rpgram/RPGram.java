package rpgram;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rpgram.core.Position;
import rpgram.core.utils.Random;
import rpgram.items.InventoryItem;
import rpgram.maps.MapLayers;

import java.util.ArrayList;
import java.util.List;

public class RPGram extends TelegramLongPollingBot {

    RPGram(DefaultBotOptions options) {
        super(options);
    }

    private ArrayList<Player> players = new ArrayList<>();

    private GlobalMap map = new GlobalMap(500, 500, 5, 3);

    void init() {
        map.generateMap();
        NPC npc = new NPC(0, "George", null, new Position(10, 10), -1);
        map.instantiateNewPlayer(npc.position, npc.getMapIcon(), -1);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (isUser(update.getMessage().getChatId())) {
                int userId = update.getMessage().getFrom().getId();
                if (!checkIfPlayerExists(userId)) {
                    Position pos = new Position(Random.randInt(0, 500), Random.randInt(0, 500));
                    while (map.layer(MapLayers.PLAYERS)[pos.x][pos.y] == '@') {
                        pos = new Position(Random.randInt(0, 500), Random.randInt(0, 500));
                    }
                    Player newPlayer = new Player(update.getMessage().getFrom().getFirstName(), pos, userId, map);
                    newPlayer.state = "";
                    newPlayer.worldState = "worldMap";
                    players.add(newPlayer);
                    if (newPlayer.worldState.equals("worldMap")) {
                        map.instantiateNewPlayer(newPlayer.getPos(), newPlayer.mapIcon, -1);
                    } else {
                        String[] worldStateSplited = newPlayer.worldState.split(" ");
                        if (worldStateSplited[0].equals("village")) {
                            map.instantiateNewPlayer(newPlayer.getPos(), newPlayer.mapIcon, Integer.parseInt(worldStateSplited[1]));
                        }
                    }

                    System.out.println("Created new player: Name=" + newPlayer.name + " id=" + newPlayer.id +
                        " position=x" + newPlayer.getPos().x + ", y" + newPlayer.getPos().y);
                } else {
                    changePos(userId);
                    ArrayList<Integer> messageSenders = getPlayer(userId).saySomethingToAll(players);
                    if (messageSenders.size() != 0) {
                        for (int id : messageSenders) {
                            sendMessage(id, getPlayer(userId).name + ": " + update.getMessage().getText());
                        }
                        return;
                    }
                }

                System.out.println(update.getMessage().getFrom().getFirstName() + ": " + update.getMessage().getText());

                SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(executePlayerCommand(
                        update.getMessage().getFrom().getId(),
                        update.getMessage().getText()
                    )).enableHtml(true);


                message.setReplyMarkup(getKeyBoardOfArrows(getPlayer(userId)));

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            Player curPlayer = getPlayer(chat_id);

            InlineKeyboardMarkup kmArrows;
            InlineKeyboardMarkup kmActions = null;
            kmArrows = getKeyBoardOfArrows(curPlayer);
            kmActions = getKeyBoardOfActionsMenu();
            switch (call_data) {

            case "go_up": {
                String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x - 1, curPlayer.getPos().y), map);
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "go_right": {
                String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x, curPlayer.getPos().y + 1), map);
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "go_left": {
                String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x, curPlayer.getPos().y - 1), map);
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "go_down": {
                String answ = curPlayer.movePlayer(new Position(curPlayer.getPos().x + 1, curPlayer.getPos().y), map);
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "inventory": {
                changePos(curPlayer.id);
                String answ = curPlayer.inventory();
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "map":
            case "back": {
                changePos(curPlayer.id);
                String answ = map.viewMapArea(curPlayer.getPos(), curPlayer.fieldOfView, getUserWorld(curPlayer));
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "getWood": {
                changePos(curPlayer.id);
                String answ = curPlayer.getResource("дерево");
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "getRock": {
                changePos(curPlayer.id);
                String answ = curPlayer.getResource("камень");
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "enter": {
                changePos(curPlayer.id);
                String answ = curPlayer.enterToVillage();
                sendEditedMessage(update, (int) message_id, answ, kmArrows);

                break;
            }
            case "getDirt": {
                changePos(curPlayer.id);
                String answ = curPlayer.getResource("землю");
                sendEditedMessage(update, (int) message_id, answ, kmArrows);
                break;
            }
            case "actions": {
                changePos(curPlayer.id);
                String answ = "Так, посмотрим, что я могу здесь сделать...";
                sendEditedMessage(update, (int) message_id, answ, kmActions);
                break;
            }
            case "talk": {
                changePos(curPlayer.id);
                String answ = "Что бы мне сказать?";
                curPlayer.state = "talking";
                sendEditedMessage(update, (int) message_id, answ, getKeyBoardOfStopTalking());
                break;
            }
            case "stop_talk": {
                changePos(curPlayer.id);
                String answ = "Так, посмотрим, что я могу здесь сделать...";
                curPlayer.state = "";
                sendEditedMessage(update, (int) message_id, answ, kmArrows);
                break;
            }
            case "exit": {
                changePos(curPlayer.id);
                String answ = curPlayer.exitFromVillage();
                sendEditedMessage(update, (int) message_id, answ, kmArrows);
                break;
            }
            case "sleep": {
                changePos(curPlayer.id);
                String answ = curPlayer.sleep();
                sendEditedMessage(update, (int) message_id, answ, kmArrows);
                break;
            }
            case "putItem": {
                changePos(curPlayer.id);
                String answ = "Что мне закопать?";
                sendEditedMessage(update, (int) message_id, answ, getKeyBoardOfListOfItems(curPlayer));
                break;
            }
            case "increase_FOV": {
                String answer = curPlayer.increaseFOV(1);
                sendEditedMessage(update, (int) message_id, answer, kmActions);
                break;
            }
            case "increase_XP": {
                String answer = curPlayer.increaseHP(5);
                sendEditedMessage(update, (int) message_id, answer, kmActions);
                break;
            }
            }
            String[] str = call_data.split(" ");
            if (str[0].equals("put")) {
                int id = Integer.parseInt(str[1]);
                Treasure treasure = null;
                for (Treasure item : map.treasures) {
                    if (item.getTreasurePosition().equals(curPlayer.getPos())) {
                        item.addNewItem(curPlayer.getInventory().get(id));
                    }
                }
                if (treasure == null) {
                    treasure = new Treasure(curPlayer.getInventory().get(id), curPlayer.getPos(), curPlayer.name);
                }
                curPlayer.inventory.remove(id);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "rpgram_dev_f1uctus_bot";
    }

    @Override
    public String getBotToken() {
        return "903900129:AAHZELemt_V2xRvzTayu3J3q7kf9hGfnLZo";
    }

    private boolean checkIfPlayerExists(int id) {
        for (Player item : players) {
            if (item.id == id) {
                return true;
            }
        }
        return false;
    }

    private String executePlayerCommand(int id, String command) {
        for (Player player : players) {
            if (player.id == id) {
                return player.name + player.executeCommand(command, map);
            }
        }
        return null;
    }

    private void changePos(int id) {
        for (Player item : players) {
            if (item.id == id) {
                if (item.getPos().x > map.getAreaWidth()) {
                    item.teleportPlayer(new Position(item.getPos().x - map.getAreaWidth(), item.getPos().y));
                }
                if (item.worldState.equals("worldMap")) {
                    map.changePlayerPos(item.oldPos, item.getPos(), item.mapIcon, -1);
                } else {
                    String[] worldStateSplited = item.worldState.split(" ");
                    if (worldStateSplited[0].equals("village")) {
                        map.changePlayerPos(item.oldPos, item.getPos(), item.mapIcon, Integer.parseInt(worldStateSplited[1]));
                    }
                }
            }
        }
    }


    private Player getPlayer(long id) {
        for (Player item : players) {
            if (item.id == id) {
                return item;
            }
        }
        return players.get(0);
    }

    private boolean isUser(long id) {
        return id > 0;
    }

    private int getUserWorld(Player player) {
        if (player.worldState.equals("worldMap")) {
            return -1;
        } else {
            String[] worldStateSplited = player.worldState.split(" ");
            if (worldStateSplited[0].equals("village")) {
                return Integer.parseInt(worldStateSplited[1]);
            }
        }
        return -1;
    }

    private void sendMessage(int idToSend, String messageToSend) {
        SendMessage message = new SendMessage()
            .setChatId((long) idToSend)
            .setText(messageToSend).enableHtml(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendEditedMessage(Update update, int message_id, String answ, InlineKeyboardMarkup km) {
        EditMessageText new_message = new EditMessageText()
            .setChatId(update.getCallbackQuery().getMessage().getChatId())
            .setMessageId(message_id)
            .setText(answ)
            .enableHtml(true)
            .setReplyMarkup(km);
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getKeyBoardOfArrows(Player player) {
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
        rowInlineFooter.add(new InlineKeyboardButton().setText("Действия").setCallbackData("actions"));
        char curChar = map.getSymbolOnPosAndLayer(player.getPos(), 3);
        if (curChar == '^') {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть дерево").setCallbackData("getWood"));
        } else if (curChar == 'o') {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Зарыть предмет").setCallbackData("putItem"));
        } else if (curChar == 'V') {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Войти в деревню").setCallbackData("enter"));
        } else if (curChar == '*') {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть камень").setCallbackData("getRock"));
        } else if (curChar == 'x') {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Выкопать сокровише").setCallbackData("getGift"));
        } else {
            if (!player.worldState.equals("worldMap")) {
                rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть землю").setCallbackData("getDirt"));
            } else {
                rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Выйти из деревни").setCallbackData("exit"));
            }
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

    private InlineKeyboardMarkup getKeyBoardOfActionsMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineTalk = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineSleep = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineLevelUp = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineBack = new ArrayList<>();
        rowInlineTalk.add(new InlineKeyboardButton().setText("Поговорить с окружающими").setCallbackData("talk"));
        rowInlineSleep.add(new InlineKeyboardButton().setText("Спать").setCallbackData("sleep"));
        rowInlineLevelUp.add(new InlineKeyboardButton().setText("Увеличить радиус зрения").setCallbackData("increase_FOV"));
        rowInlineLevelUp.add(new InlineKeyboardButton().setText("Увеличить здоровье").setCallbackData("increase_XP"));
        rowInlineBack.add(new InlineKeyboardButton().setText("Назад").setCallbackData("back"));
        rowsInline.add(rowInlineTalk);
        rowsInline.add(rowInlineSleep);
        rowsInline.add(rowInlineLevelUp);
        rowsInline.add(rowInlineBack);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardMarkup getKeyBoardOfStopTalking() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineUp = new ArrayList<>();
        rowInlineUp.add(new InlineKeyboardButton().setText("Закончить разговор").setCallbackData("stop_talk"));
        rowsInline.add(rowInlineUp);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardMarkup getKeyBoardOfListOfItems(Player player) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        int counter = 0;
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (InventoryItem item : player.getInventory()) {
            rowInline.add(new InlineKeyboardButton().setText(item.getName()).setCallbackData("put " + counter));
            rowsInline.add(rowInline);
            counter++;
        }
        rowInline.add(new InlineKeyboardButton().setText("Отмена").setCallbackData("remove_status_to_zero"));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

}
