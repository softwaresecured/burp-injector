package burp_injector.constants;

/**
 * Auto target regexes
 * https://community.notepad-plus-plus.org/topic/25302/proposed-faq-explaining-why-you-shouldn-t-parse-json-with-regex/10
 * For JSON we nuke it from orbit. Only way to be sure.
 */
public class TargetingConstants {
    public static final String XML_AUTO_TARGET = "<\\s?([^<>\\s/]{1,255})[^>]*>\\s*([^<]*)</";
    public static final String JSON_AUTO_TARGET = "(((?<!(?<!\\\\)\\\\))\"((?:\\\\\\\\|\\\\\"|[^\"\\r\\n])*(?<!(?<!\\\\)\\\\))\")";
    public static final String KVP_AUTO_TARGET = "([^&=]{1,255})(?:=([^&=]*))?";
    public static final String ALL_QUOTED_VALUES_TARGET = "[\"'](.*?)[\"']";
    public static final String CSV_VALUES_TARGET = "(?:,\"'|^\"')(\"\"|[\\w\\W]*?)(?=\"',|\"'$)|(?:,(?!\"')|^(?!\"'))([^,]*?)(?=$|,)|(\\r\\n|\\n)";

    public static final String KVP_MATCHER = "^[^&]+=.*$";
    public static final String CSV_MATCHER = "['\"]+[^,\"']+['\"]+,";
}
