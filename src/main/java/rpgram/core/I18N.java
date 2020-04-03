package rpgram.core;

import rpgram.core.utils.Random;

import java.text.MessageFormat;
import java.util.*;


public class I18N {
    public static ResourceBundle rb;
    public static final HashMap<String, ResourceBundle> bundles = new HashMap<>();

    public static void init() {
        bundles.put("ru", ResourceBundle.getBundle("gameMessages", new Locale("ru_RU")));
        bundles.put("en", ResourceBundle.getBundle("gameMessages", new Locale("en_US")));

        rb = bundles.get("ru");
        if (rb == null) {
            rb = bundles.get("ru");
        }
    }

    /**
     * Returns a message from given resource key.
     */
    public static String get(String resourceKey) {
        return rb.getString(resourceKey);
    }

    /**
     * Returns a message from given formatted resource key, formatted with given objects.
     * e.g. if in resource key is `msg.got=Got {0}`, and get("msg.got", "stone") is invoked,
     * method will return "Got stone".
     */
    public static String get(String resourceKey, Object... format) {
        // noinspection RedundantCast
        return String.format(rb.getString(resourceKey), (Object[]) format);
    }

    /**
     * Returns change response message from given formatted resource key.
     * If delta > 0, {0} is replaced with `increased`
     * Otherwise, {0} becomes `decreased`.
     * e.g. getChangeable("stats.hp.{0}", -5) will return a message with key "stats.hp.decreased".
     */
    public static String getChangeable(String resourceKey, int delta) {
        String result;
        if (delta > 0) {
            result = rb.getString(MessageFormat.format(resourceKey, "increased"));
        } else {
            result = rb.getString(MessageFormat.format(resourceKey, "decreased"));
        }
        return MessageFormat.format(result, delta);
    }

    /**
     * Returns a random message from given formatted resource key.
     * e.g. getRandom("msg.{0}") will return any message with key msg.1, msg.2, etc.
     */
    public static String getRandom(String resourceKey) {
        List<String> messages = new ArrayList<>();
        int i = 0;
        String key = MessageFormat.format(resourceKey, i);
        while (rb.containsKey(key)) {
            messages.add(rb.getString(key));
            key = MessageFormat.format(resourceKey, i);
            i++;
        }
        if (messages.size() == 0) {
            return null;
        }
        return messages.get(Random.rnd.nextInt(messages.size()));
    }

    public static void setLang(String shortLangName) {
        ResourceBundle newRb = bundles.get(shortLangName);
        if (newRb != null) {
            rb = newRb;
        }
    }
}
