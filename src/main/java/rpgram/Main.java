package rpgram;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rpgram.core.Config;
import rpgram.core.I18N;

public class Main {

    private static final String PROXY_HOST = "127.0.0.1";
    private static final Integer PROXY_PORT = 9050;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("No configuration file specified.");
        }
        I18N.init();
        Config config = new Config(args[0]);
        System.out.println("Loading config...");
        config.load();

        String isProxyOn = config.get("proxy.enabled");
        String proxyHost = config.get("proxy.host");
        String proxyPort = config.get("proxy.port");

        System.out.println("Starting server...");
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        if (isProxyOn == null || Boolean.parseBoolean(isProxyOn)) {
            botOptions.setProxyHost((proxyHost != null) ? proxyHost : PROXY_HOST);
            botOptions.setProxyPort((proxyPort != null) ? Integer.parseInt(proxyPort) : PROXY_PORT);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        }

        RPGram bot = new RPGram(botOptions, config);
        try {
            System.out.println("Connecting to Telegram...");
            botsApi.registerBot(bot);
            System.out.println("Successful connection to Telegram");
        } catch (TelegramApiException e) {
            System.out.println("ERROR: Could not register the bot: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Server successfully started!");
    }
}
