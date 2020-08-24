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
    public static final Config config = new Config();

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("No configuration file specified.");
        }

        System.out.println("Initializing resources...");
        bundles.put("ru", ResourceBundle.getBundle("gameMessages", new Locale("ru_RU")));
        bundles.put("en", ResourceBundle.getBundle("gameMessages", new Locale("en_US")));
        I18n.init(bundles);
        System.out.println("Resources initialized.");

        System.out.println("Loading config...");
        config.load(args[0]);
        System.out.println("Config loaded.");

        GameState gameState;
        System.out.println("Constructing new game state...");
        int mapSize = config.getMapSize();
        gameState = new GameState(
            new GlobalMap("Global map", mapSize, mapSize, MapLevel.height)
        );
        System.out.println("Game state created.");

        System.out.println("Constructing main timeline...");
        Timeline.init(
            new VirtualClock(1000, () -> {
            }).startAtRnd(),
            gameState
        );
        System.out.println("Main timeline created.");

        System.out.println("Loading Telegram API...");
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        if (config.isProxyEnabled()) {
            botOptions.setProxyHost(config.getProxyHost());
            botOptions.setProxyPort(config.getProxyPort());
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        }
        System.out.println("Telegram API loaded.");

        RPGram bot = new RPGram(botOptions, config, gameState);

        try {
            System.out.println("Connecting to Telegram...");
            botsApi.registerBot(bot);
            System.out.println("Successful connection to Telegram.");
        } catch (TelegramApiException e) {
            System.out.println("Failed to connect to Telegram.");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Server successfully started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down a server...");
            // TODO: serialize main timeline
        }));
    }
}
