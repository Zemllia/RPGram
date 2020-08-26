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
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
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
                var messageText = update.getMessage().getText();
                int userId = update.getMessage().getFrom().getId();
                var userName = update.getMessage().getFrom().getUserName();
                var nameAndText = userName + ": " + messageText;
                Human p;
                if (gameState.hasPlayer(userId)) {
                    p = gameState.getPlayer(userId);
                    // do not broadcast user commands to other players
                    if (!messageText.startsWith("/")) {
                        var messageReceivers = p.getMap().getAll(
                            Human.class,
                            p.getPt0(),
                            p.getFov()
                        );
                        for (var receiver : messageReceivers) {
                            if (receiver != p) {
                                sendMessage(
                                    (int) receiver.telegramId,
                                    nameAndText
                                );
                            }
                        }
                    }
                } else {
                    p = gameState.addPlayer(userId, userName);
                    System.out.println("New player: " + p);
                }

                System.out.println(nameAndText);

                var cmdArray = messageText.toLowerCase().trim().split(" ");

                var answer = I18n.empty;
                switch (cmdArray[0]) {
                case "/tp":
                    var absPoint = new Point3D(
                        Integer.parseInt(cmdArray[1]),
                        Integer.parseInt(cmdArray[2]),
                        0
                    ).minus(p.getPt0());
                    answer = p.move(absPoint.x, absPoint.y, 0);
                    break;
                case "/start":
                    answer = I18n.of("rpgram.welcome");
                    break;
                }

                var message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(MapView.markup(p, answer))
                    .enableHtml(true)
                    .setReplyMarkup(Keyboards.arrows(p));

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
                var result = player.moveBy(0, -1);
                answer = MapView.markup(player, result);
                keyboard = Keyboards.arrows(player);
            }
            // move ->
            else if (Button.right.getCallback().equals(callbackData)) {
                var result = player.moveBy(1, 0);
                answer = MapView.markup(player, result);
                keyboard = Keyboards.arrows(player);
            }
            // move <-
            else if (Button.left.getCallback().equals(callbackData)) {
                var result = player.moveBy(-1, 0);
                answer = MapView.markup(player, result);
                keyboard = Keyboards.arrows(player);
            }
            // move v
            else if (Button.down.getCallback().equals(callbackData)) {
                var result = player.moveBy(0, 1);
                answer = MapView.markup(player, result);
                keyboard = Keyboards.arrows(player);
            }

            // region actions menu

            else if (Button.actions.getCallback().equals(callbackData)) {
                answer = I18n.of("rpgram.whatCanIDo").getLocalized(player.lang);
                keyboard = Keyboards.actionsMenu(player);
            }
            // upgrade fov
            else if (Button.upgradeFov.getCallback().equals(callbackData)) {
                answer = player.changeFovBy(1).getLocalized(player.lang);
                keyboard = Keyboards.actionsMenu(player);
            }
            // upgrade hp
            else if (Button.upgradeHp.getCallback().equals(callbackData)) {
                answer = player.changeHpBy(1).getLocalized(player.lang);
                keyboard = Keyboards.actionsMenu(player);
            }
            // show inventory
            else if (Button.inventory.getCallback().equals(callbackData)) {
                answer = player.getStats().getLocalized(player.lang);
                keyboard = Keyboards.arrows(player);
            }
            // sleep
            else if (Button.sleep.getCallback().equals(callbackData)) {
                var result = player.sleep();
                answer = MapView.markup(player, result);
                keyboard = Keyboards.arrows(player);
            }
            // show main view (map + stats)
            else if (Button.map.getCallback().equals(callbackData)
                || Button.back.getCallback().equals(callbackData)) {
                answer = MapView.markup(player);
                keyboard = Keyboards.arrows(player);
            }

            // endregion

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
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse().contains("message is not modified")) return;
            e.printStackTrace();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
