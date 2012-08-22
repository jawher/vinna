package vinna.util;

import vinna.VinnaContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class VinnaMessagesControl extends ResourceBundle.Control {

    @Override
    public List<String> getFormats(String baseName) {
        // May the gods forgive me for doing this
        return Arrays.asList(VinnaContext.get().vinna.getBasePackage().replace(".", "/"), "vinna");
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        String name = toBundleName(baseName, locale) + ".properties";
        final String path = format + "/" + name;
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (stream == null) {
            return null;
        } else {
            return new PropertyResourceBundle(new InputStreamReader(stream, "utf-8"));
        }
    }
}
