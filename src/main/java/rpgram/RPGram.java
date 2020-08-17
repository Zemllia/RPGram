package rpgram;

import com.crown.i18n.I18n;
import com.crown.maps.MapIcon;
import com.crown.maps.Point3D;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rpgram.core.Config;
import rpgram.creatures.Human;
import rpgram.ui.Button;
import rpgram.ui.Keyboards;

public class RPGram extends TelegramLongPollingBot {
    private final Config config;
    private final GameState gameState;

    RPGram(DefaultBotOptions options, Config config, GameState gameState) {
        super(options);
        this.config = config;
        this.gameState = gameState;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (isUser(update.getMessage().getChatId())) {
                int userId = update.getMessage().getFrom().getId();
                Human p;
                if (!gameState.hasPlayer(userId)) {
                    var newPlayerName = update.getMessage().getFrom().getFirstName();
                    p = gameState.addPlayer(userId, newPlayerName);
                    System.out.println("Created a new player:" + p);
                } else {
                    p = gameState.getPlayer(userId);
//                    ArrayList<Integer> messageSenders = p.saySomethingToAll(players);
//                    if (messageSenders.size() != 0) {
//                        for (int id : messageSenders) {
//                            sendMessage(id, p.getName() + ": " + update.getMessage().getText());
//                        }
//                        return;
//                    }
                }

                System.out.println(update.getMessage().getFrom().getFirstName() + ": " + update.getMessage().getText());

                String command = update.getMessage().getText();
                String[] commandArray = command.toLowerCase().trim().split(" ");

                StringBuilder answer = new StringBuilder();
                if (commandArray[0].equals("тп")) {
                    var absPoint = new Point3D(
                        Integer.parseInt(commandArray[1]),
                        Integer.parseInt(commandArray[2]),
                        0
                    ).minus(p.getPt0());
                    p.move(absPoint.x, absPoint.y, absPoint.z);
                    answer.append(I18n.of("rpgram.tpUsed"));
                } else if (commandArray[0].equals("/start")) {
                    answer.append(I18n.of("rpgram.welcome"));
                }

                SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(p.getName() + ": " + answer).enableHtml(true);

                message.setReplyMarkup(Keyboards.arrows(p));

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
            Human player = gameState.getPlayer(chat_id);
            InlineKeyboardMarkup keyboard = null;
            String answer = null;
            // move ^
            if (Button.up.getCallback().equals(call_data)) {
                player.moveBy(0, -1);
                answer = statsToMarkup(player) + "\n" + mapToMarkup(player);
                keyboard = Keyboards.arrows(player);
            }
            // move ->
            else if (Button.right.getCallback().equals(call_data)) {
                player.moveBy(1, 0);
                answer = statsToMarkup(player) + "\n" + mapToMarkup(player);
                keyboard = Keyboards.arrows(player);
            }
            // move <-
            else if (Button.left.getCallback().equals(call_data)) {
                player.moveBy(-1, 0);
                answer = statsToMarkup(player) + "\n" + mapToMarkup(player);
                keyboard = Keyboards.arrows(player);
            }
            // move v
            else if (Button.down.getCallback().equals(call_data)) {
                player.moveBy(0, 1);
                answer = statsToMarkup(player) + "\n" + mapToMarkup(player);
                keyboard = Keyboards.arrows(player);
            }
            // show inventory
            else if (Button.inventory.getCallback().equals(call_data)) {
                answer = player.getStats().getLocalized(player.lang);
                keyboard = Keyboards.arrows(player);
            }
            // show main view (map + stats)
            else if (Button.map.getCallback().equals(call_data) || Button.back.getCallback().equals(call_data)) {
                answer = statsToMarkup(player) + "\n" + mapToMarkup(player);
                keyboard = Keyboards.arrows(player);
            }
            // sleep
            else if (Button.sleep.getCallback().equals(call_data)) {
                answer = player.sleep().getLocalized(player.lang);
                keyboard = Keyboards.arrows(player);
            }
            sendEditedMessage(update, (int) message_id, answer, keyboard);
        }
    }

    @Override
    public String getBotUsername() {
        return this.config.get("bot.name");
    }

    @Override
    public String getBotToken() {
        return this.config.get("bot.token");
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

    private void sendEditedMessage(Update update, int message_id, String answer, InlineKeyboardMarkup km) {
        EditMessageText new_message = new EditMessageText()
            .setChatId(update.getCallbackQuery().getMessage().getChatId())
            .setMessageId(message_id)
            .setText(answer)
            .enableHtml(true)
            .setReplyMarkup(km);
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String statsToMarkup(Human player) {
        return "[" + player.getPt0().x + ", " + player.getPt0().y + "]"
            + " LVL: " + player.getLevel() + "/" + player.getXp()
            + " HP: " + player.getHp()
            + " E: " + player.getEnergy();
    }

    private String mapToMarkup(Human player) {
        var area = player.getMap().get2DArea(
            player.getPt0(),
            player.getFov()
        );
        StringBuilder answer = new StringBuilder();
        answer.append("<code>\n");
        answer.append("\n");
        for (MapIcon<?>[] mapIcons : area) {
            for (int x = 0; x < area[0].length; x++) {
                answer.append(mapIcons[x].get()).append(" ");
            }
            answer.append("\n");
        }
        answer.append("\n</code>");
        return answer.toString();
    }
}
