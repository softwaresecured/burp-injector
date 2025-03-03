package burp_injector.targeting;

import burp_injector.constants.TargetingConstants;
import burp_injector.util.Logger;
import burp_injector.util.RegexUtil;

import java.util.regex.Pattern;

/**
 * Attempts to find th best pre-built regex for a given payload
 */

public class AutoTargeter {
    private String content = null;
    private Pattern targetPattern = null;
    private int valueCaptureGroup = 2;
    private String autoTargetMatchFormat = null;

    public AutoTargeter(String content) {
        this.content = content;
        if ( content != null ) {
            selectPattern();
        }
    }

    public String getAutoTargetMatchFormat() {
        return autoTargetMatchFormat;
    }

    public Pattern getTargetPattern() {
        return targetPattern;
    }

    public int getValueCaptureGroup() {
        return valueCaptureGroup;
    }

    private void selectPattern() {
        if ( content.startsWith("{")) {
            targetPattern = Pattern.compile(TargetingConstants.JSON_AUTO_TARGET);
            valueCaptureGroup = 3;
            autoTargetMatchFormat = "JSON";
        }
        else if ( content.startsWith("<")) {
            targetPattern = Pattern.compile(TargetingConstants.XML_AUTO_TARGET);
            autoTargetMatchFormat = "XML";
        }
        else if ( RegexUtil.checkMatch(TargetingConstants.CSV_MATCHER,content)) {
            targetPattern = Pattern.compile(TargetingConstants.CSV_VALUES_TARGET);
            autoTargetMatchFormat = "CSV";
        }
        else if (RegexUtil.checkMatch(TargetingConstants.KVP_MATCHER,content)) {
            targetPattern = Pattern.compile(TargetingConstants.KVP_AUTO_TARGET);
            autoTargetMatchFormat = "KVP";
        }
        else {
            autoTargetMatchFormat = "QUOTED_STRINGS";
            targetPattern = Pattern.compile(TargetingConstants.ALL_QUOTED_VALUES_TARGET);
        }
    }
}
