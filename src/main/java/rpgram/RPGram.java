package rpgram;

import com.crown.i18n.I18n;
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
import rpgram.ui.views.MapView;

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
                    var newPlayerName = update.getMessage().getFrom().getUserName();
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

                System.out.println(update.getMessage().getFrom().getUserName() + ": " + update.getMessage().getText());

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
            var callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            long chatId = callbackQuery.getMessage().getChatId();
            Human player = gameState.getPlayer(chatId);
            InlineKeyboardMarkup keyboard = null;
            String answer = null;
            // move ^
            if (Button.up.getCallback().equals(callbackData)) {
                player.moveBy(0, -1);
                answer = MapView.markup(player);
                keyboard = Keyboards.arrows(player);
            }
            // move ->
            else if (Button.right.getCallback().equals(callbackData)) {
                player.moveBy(1, 0);
                answer = MapView.markup(player);
                keyboard = Keyboards.arrows(player);
            }
            // move <-
            else if (Button.left.getCallback().equals(callbackData)) {
                player.moveBy(-1, 0);
                answer = MapView.markup(player);
                keyboard = Keyboards.arrows(player);
            }
            // move v
            else if (Button.down.getCallback().equals(callbackData)) {
                player.moveBy(0, 1);
                answer = MapView.markup(player);
                keyboard = Keyboards.arrows(player);
            }
            // show inventory
            else if (Button.inventory.getCallback().equals(callbackData)) {
                answer = player.getStats().getLocalized(player.lang);
                keyboard = Keyboards.arrows(player);
            }
            // show main view (map + stats)
            else if (Button.map.getCallback().equals(callbackData) || Button.back.getCallback().equals(callbackData)) {
                answer = MapView.markup(player);
                keyboard = Keyboards.arrows(player);
            }
            // sleep
            else if (Button.sleep.getCallback().equals(callbackData)) {
                answer = player.sleep().getLocalized(player.lang);
                keyboard = Keyboards.arrows(player);
            }
            long messageId = callbackQuery.getMessage().getMessageId();
            sendEditedMessage(update, (int) messageId, answer, keyboard);
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
}
