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
import rpgram.creatures.Creature;
import rpgram.creatures.NPC;
import rpgram.creatures.Player;
import rpgram.creatures.PlayerState;
import rpgram.items.InventoryItem;
import rpgram.maps.GlobalMap;
import rpgram.maps.MapLayers;
import rpgram.maps.MapLegend;

import java.util.ArrayList;
import java.util.List;

public class RPGram extends TelegramLongPollingBot {
    private ArrayList<Creature> players = new ArrayList<>();

    private GlobalMap globalMap;

    RPGram(DefaultBotOptions options) {
        super(options);
        globalMap = new GlobalMap(500, 500, 5, 3);
        NPC npc = new NPC(0, "George", null, new Position(10, 10), globalMap);
        players.add(npc);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (isUser(update.getMessage().getChatId())) {
                int userId = update.getMessage().getFrom().getId();
                Player p;
                if (!checkIfPlayerExists(userId)) {
                    Position pos = new Position(Random.randInt(0, 500), Random.randInt(0, 500));
                    // TODO: check this condition
                    while (Character.isAlphabetic(globalMap.getChar(MapLayers.PLAYERS, pos))) {
                        pos = new Position(Random.randInt(0, 500), Random.randInt(0, 500));
                    }
                    p = new Player(update.getMessage().getFrom().getFirstName(), pos, userId, globalMap);
                    players.add(p);
                    globalMap.addObject(p);

                    System.out.println("Created a new player: Name=" + p.getName() + " id=" + p.getId() +
                        " position=x" + p.getPos().x + ", y=" + p.getPos().y);
                } else {
                    p = getPlayer(userId);
                    ArrayList<Integer> messageSenders = p.saySomethingToAll(players);
                    if (messageSenders.size() != 0) {
                        for (int id : messageSenders) {
                            sendMessage(id, p.getName() + ": " + update.getMessage().getText());
                        }
                        return;
                    }
                }

                System.out.println(update.getMessage().getFrom().getFirstName() + ": " + update.getMessage().getText());

                String command = update.getMessage().getText();
                String[] commandArray = command.toLowerCase().trim().split(" ");

                StringBuilder answer = new StringBuilder();
                if (commandArray[0].equals("тп")) {
                    p.teleport(new Position(Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2])));
                    answer.append("Вы заюзали дебаг функцию, админы уже заметили это, за вами выехал магический спецназ!");
                } else if (commandArray[0].equals("/start")) {
                    answer.append("Привет от RPGram!");
                }

                SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(p.getName() + ": " + answer).enableHtml(true);

                message.setReplyMarkup(getKeyBoardOfArrows(p));

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

            InlineKeyboardMarkup keyboard = null;
            String answer = "";
            switch (call_data) {

            case "go_up": {
                answer = curPlayer.move(new Position(curPlayer.getPos().x - 1, curPlayer.getPos().y));
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "go_right": {
                answer = curPlayer.move(new Position(curPlayer.getPos().x, curPlayer.getPos().y + 1));
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "go_left": {
                answer = curPlayer.move(new Position(curPlayer.getPos().x, curPlayer.getPos().y - 1));
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "go_down": {
                answer = curPlayer.move(new Position(curPlayer.getPos().x + 1, curPlayer.getPos().y));
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "inventory": {
                answer = curPlayer.getStats();
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "map":
            case "back": {
                answer = curPlayer.getMap().viewMapArea(curPlayer.getPos(), curPlayer.getFieldOfView());
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "getWood": {
                answer = curPlayer.getResource("дерево");
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "getRock": {
                answer = curPlayer.getResource("камень");
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "enter": {
                answer = curPlayer.enterVillage();
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "getDirt": {
                answer = curPlayer.getResource("землю");
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "actions": {
                answer = "Так, посмотрим, что я могу здесь сделать...";
                keyboard = getKeyBoardOfActionsMenu();
                break;
            }
            case "talk": {
                answer = "Что бы мне сказать?";
                curPlayer.state = PlayerState.TALKING;
                keyboard = getKeyBoardOfStopTalking();
                break;
            }
            case "stop_talk": {
                answer = "Так, посмотрим, что я могу здесь сделать...";
                curPlayer.state = PlayerState.NORMAL;
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "exit": {
                answer = curPlayer.exitVillage();
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "sleep": {
                answer = curPlayer.sleep();
                keyboard = getKeyBoardOfArrows(curPlayer);
                break;
            }
            case "putItem": {
                answer = "Что мне закопать?";
                keyboard = getKeyBoardOfListOfItems(curPlayer);
                break;
            }
            case "increase_FOV": {
                answer = curPlayer.increaseFOV(1);
                keyboard = getKeyBoardOfActionsMenu();
                break;
            }
            case "increase_XP": {
                answer = curPlayer.increaseHP(5);
                keyboard = getKeyBoardOfActionsMenu();
                break;
            }
            default: {
                String[] str = call_data.split(" ");
                if (str[0].equals("put") && curPlayer.getMap() instanceof GlobalMap) {
                    int id = Integer.parseInt(str[1]);
                    Treasure treasure = null;
                    for (Treasure item : ((GlobalMap) curPlayer.getMap()).treasures) {
                        if (item.getTreasurePosition().equals(curPlayer.getPos())) {
                            item.addNewItem(curPlayer.getInventory().get(id));
                        }
                    }
                    if (treasure == null) {
                        treasure = new Treasure(curPlayer.getInventory().get(id), curPlayer.getPos(), curPlayer.getName());
                    }
                    curPlayer.inventory.remove(id);
                    answer = "Зарыл " + treasure.getInventoryItem().get(0).getName();
                    keyboard = getKeyBoardOfArrows(curPlayer);
                }
            }
            }
            sendEditedMessage(update, (int) message_id, answer, keyboard);
        }
    }

    @Override
    public String getBotUsername() {
        return "RPGram_bot";
    }

    @Override
    public String getBotToken() {
        return "658606256:AAG3O_p83oGSI8feIGLFadJFWzZY4rbch4c";
    }

    public boolean checkIfPlayerExists(int id) {
        return getPlayer(id) != null;
    }


    public Player getPlayer(long id) {
        for (Creature creature : players) {
            if (creature instanceof Player && creature.getId() == id) {
                return (Player) creature;
            }
        }
        return null;
    }

    public boolean isUser(long id) {
        return id > 0;
    }

    public void sendMessage(int idToSend, String messageToSend) {
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
        char curChar = player.getMap().getChar(MapLayers.ENVIRONMENT, player.getPos());
        if (curChar == MapLegend.TREE.getValue()) {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть дерево").setCallbackData("getWood"));
        } else if (curChar == MapLegend.GROUNDHOLE.getValue()) {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Зарыть предмет").setCallbackData("putItem"));
        } else if (curChar == MapLegend.VILLAGE.getValue()) {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Войти в деревню").setCallbackData("enter"));
        } else if (curChar == MapLegend.ROCK.getValue()) {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Добыть камень").setCallbackData("getRock"));
        } else if (curChar == MapLegend.TREASURE.getValue()) {
            rowInlineUnderFooter.add(new InlineKeyboardButton().setText("Выкопать сокровише").setCallbackData("getGift"));
        } else {
            if (player.getMap() instanceof GlobalMap) {
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
