package simplewebcrawler.validator;

import java.net.URL;

public class URLValidator {
    public static boolean isValid(URL url) {
        return url != null && url.getProtocol().startsWith("http") && !url.getProtocol().endsWith("#");
    }
}
