package vinna;

import vinna.VinnaContext;
import vinna.util.VinnaMessagesControl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {
    private static final ResourceBundle.Control CONTROL = new VinnaMessagesControl();

    public static String get(Locale locale, String key) {
        return ResourceBundle.getBundle("messages", locale, CONTROL).getString(key);
    }

    public static String get(String key) {
        return get(VinnaContext.get().vinna.getUserLocale(), key);
    }

    public static String format(String key, Object... args) {
        final Locale locale = VinnaContext.get().vinna.getUserLocale();
        return format(locale, key, args);
    }

    public static String format(Locale locale, String key, Object... args) {
        MessageFormat fmt = new MessageFormat(get(key), locale);
        return fmt.format(args);
    }
}
