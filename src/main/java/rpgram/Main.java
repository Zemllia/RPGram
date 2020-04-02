package rpgram;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import rpgram.core.Config;

public class Main {

    private static String PROXY_HOST = "127.0.0.1" /* proxy host */;
    private static Integer PROXY_PORT = 9050 /* proxy port */;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("No configuration file is specified.");
        }
        Config config = new Config(args[0]);
        System.out.println("Loading config...");
        config.load();

        String proxyHost = config.get("proxy.host");
        String proxyPort = config.get("proxy.port");

        System.out.println("Starting server...");
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        botOptions.setProxyHost((proxyHost != null) ? proxyHost : PROXY_HOST);
        botOptions.setProxyPort((proxyPort != null) ?
                                Integer.parseInt(proxyPort) : PROXY_PORT);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        RPGram bot = new RPGram(botOptions, config);
        try {
            System.out.println("Connecting to Telegram...");
            botsApi.registerBot(bot);
            System.out.println("Successful connection to Telegram");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("Server successfully started!");
    }
}
