package rpgram.core;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    public static ResourceBundle rb;
    public static final HashMap<String, ResourceBundle> bundles = new HashMap<>();

    public static void init() {
        bundles.put("ru", ResourceBundle.getBundle("gameMessages", new Locale("ru_RU")));
        bundles.put("en", ResourceBundle.getBundle("gameMessages", new Locale("en_US")));

        rb = bundles.get(Locale.getDefault().getLanguage());
        if (rb == null) {
            rb = bundles.get("ru");
        }
    }

    public static String get(String resourceKey) {
        return rb.getString(resourceKey);
    }

    public static String get(String resourceKey, Object... format) {
        // noinspection RedundantCast
        return String.format(rb.getString(resourceKey), (Object[]) format);
    }

    public static String getChangeable(String resourceKey, int delta) {
        String result;
        if (delta > 0) {
            result = rb.getString(MessageFormat.format(resourceKey, "increased"));
        } else {
            result = rb.getString(MessageFormat.format(resourceKey, "decreased"));
        }
        return MessageFormat.format(result, delta);
    }

    public static void setLang(String shortLangName) {
        ResourceBundle newRb = bundles.get(shortLangName);
        if (newRb != null) {
            rb = newRb;
        }
    }
}
