package burp_injector.util;

import burp_injector.model.data.InjectorRule;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for Injector rules
 */
public class InjectorUtil {
    /**
     * Extracts a target area for a given request ( as string )
     * @param requestAsString The HTTP request as a string
     * @param pattern The regex pattern isolating the target area
     * @param captureGroup The capture group to extract
     * @return The target area as a string
     */
    public static String extractTargetArea(String requestAsString, Pattern pattern, int captureGroup ) {
        Matcher m = pattern.matcher(requestAsString);
        if ( m.find()  ) {
            if ( m.groupCount() >= captureGroup ) {
                return m.group(captureGroup);
            }
        }
        return null;
    }

    public static ArrayList<String> getRuleNames(ArrayList<InjectorRule> rules ) {
        ArrayList<String> ruleNames = new ArrayList<String>();
        for ( InjectorRule rule : rules ) {
            if ( !ruleNames.contains(rule.getName())) {
                ruleNames.add(rule.getName());
            }
        }
        return ruleNames;
    }
}
