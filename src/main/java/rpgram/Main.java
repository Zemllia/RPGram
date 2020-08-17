package rpgram;

import com.crown.i18n.I18n;
import com.crown.time.Timeline;
import com.crown.time.VirtualClock;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rpgram.core.Config;
import rpgram.maps.GlobalMap;
import rpgram.maps.MapLevel;

import java.util.*;

public class Main {
    public static final HashMap<String, ResourceBundle> bundles = new HashMap<>();
    public static final int MAP_SIZE = 101;

    private static final String PROXY_HOST = "127.0.0.1";
    private static final Integer PROXY_PORT = 9050;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("No configuration file specified.");
        }

        System.out.println("Loading resources...");
        bundles.put("ru", ResourceBundle.getBundle("gameMessages", new Locale("ru_RU")));
        bundles.put("en", ResourceBundle.getBundle("gameMessages", new Locale("en_US")));
        I18n.init(bundles);

        System.out.println("Loading config...");
        Config config = new Config(args[0]);
        config.load();

        String isProxyOn = config.get("proxy.enabled");
        String proxyHost = config.get("proxy.host");
        String proxyPort = config.get("proxy.port");

        System.out.println("Constructing game state...");
        var gameState = new GameState(
            new GlobalMap(
                "Global map",
                MAP_SIZE,
                MAP_SIZE,
                MapLevel.height
            ));

        System.out.println("Constructing main timeline...");
        new Thread(() -> {
            Timeline.init(
                new VirtualClock(1000, () -> {
                }).startAtRnd(),
                gameState
            );
        });

        System.out.println("Starting server...");
        ApiContextInitializer.init();

        System.out.println("Loading Telegram API...");
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
            e.printStackTrace();
        }
        System.out.println("Server successfully started!");
    }
}
