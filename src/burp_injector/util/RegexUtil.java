package burp_injector.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Utility functions for regexes
 */
public class RegexUtil {
    /**
     * Validate a regex
     * @param regex The regex to validate
     * @return True or false depending on validity
     */
    public static boolean validateRegex( String regex ) {
        try {
            Pattern.compile(regex);
            return true;
        } catch ( PatternSyntaxException e ) {}

        return false;
    }

    /**
     * Get the number of capture groups in a given regex
     * @param regex The regex that contains capture groups
     * @return The number of capture groups
     */
    public static int getMatchGroupCount( String regex ) {
        if ( validateRegex(regex)) {
            return Pattern.compile(regex).matcher("").groupCount();
        }
        return 0;
    }

    /**
     * Checks if a regex matches a string
     * @param regex The regex
     * @param content The content on which to apply the regex
     * @return True if match false if not
     */
    public static boolean checkMatch( String regex, String content ) {
        Pattern p = Pattern.compile(regex,Pattern.DOTALL|Pattern.MULTILINE);
        Matcher m = p.matcher(content);
        m.find();
        return m.hasMatch();
    }

    /**
     * Checks if a regex matches a string
     * @param pattern The compiled regex to use
     * @param content The content on which to apply the regex
     * @return True if match false if not
     */
    public static boolean checkMatch( Pattern pattern, String content ) {
        Matcher m = pattern.matcher(content);
        m.find();
        return m.hasMatch();
    }
}
