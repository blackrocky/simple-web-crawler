package simplewebcrawler.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class URLStringValidator {
    private static final Pattern EXCLUDED_EXTENSIONS = Pattern.compile(".*\\.(bat|exe|cmd|sh|php([0-9])?|pl|cgi|386|dll|com|torrent|js|app|jar|pif|vb|vbscript|wsf|asp|cer|csr|jsp|drv|sys|ade|adp|bas|chm|cpl|crt|csh|fxp|hlp|hta|inf|ins|isp|jse|htaccess|htpasswd|ksh|lnk|mdb|mde|mdt|mdw|msc|msi|msp|mst|ops|pcd|prg|reg|scr|sct|shb|shs|url|vbe|vbs|wsc|wsf|wsh)$");

    public boolean isValid(String urlString) {
        return !StringUtils.isBlank(urlString) && !StringUtils.endsWith(urlString, "#") && !excludedExtensions(urlString);
    }

    private boolean excludedExtensions(String urlString) {
        return EXCLUDED_EXTENSIONS.matcher(urlString).matches();
    }
}
