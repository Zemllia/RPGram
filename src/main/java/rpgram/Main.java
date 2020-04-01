import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    private static String PROXY_HOST = "127.0.0.1" /* proxy host */;
    private static Integer PROXY_PORT = 9050 /* proxy port */;

    public static void main(String[] args) {
        System.out.println("Starting server...");
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        //botOptions.setProxyHost(PROXY_HOST);
        //botOptions.setProxyPort(PROXY_PORT);
        //botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        RPGram bot = new RPGram(botOptions);
        try {
            System.out.println("Connecting to Telegram...");
            botsApi.registerBot(bot);
            System.out.println("Successful connection to Telegram");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        bot.init();
        System.out.println("Server successfully started!");
    }
}
