import com.google.inject.internal.cglib.core.$ReflectUtils;
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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if(isUser(update.getMessage().getChatId())) {
                int userId = update.getMessage().getFrom().getId();
                //System.out.println(checkIfExists(userId));
                if(!checkIfExists(userId)){
                    players.add(new Player(update.getMessage().getFrom().getFirstName(), userId));
                }
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText(executePlayerCommand(update.getMessage().getFrom().getId(), update.getMessage().getText()));
                try {
                    execute(message); // Call method to send the message
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
        return "658606256:AAE7nTrlZHmlqJ-gLJlOFau9iOGANmMtVVU";
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
                return item.executeCommand(command);
            }
        }
        return "Шото непонятно...";
    }

    public boolean isUser(long id){
        System.out.println(id);
        return id > 0;
    }
}

