package burp_injector.model.data;

import burp_injector.config.InjectorTargetExport;
import burp_injector.util.Logger;
import burp_injector.util.RegexUtil;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Injector target object
 */
public class InjectorTarget {
    private String id = UUID.randomUUID().toString();
    private String targetName = null;
    private String targetRegex = null;
    private Pattern targetRegexPattern = null;
    private int targetValueCaptureGroup = 1;

    public InjectorTarget() {
    }

    public InjectorTarget(String id, String targetName, String targetRegex, int targetValueCaptureGroup) {
        if ( id != null ) {
            this.id = id;
        }
        this.targetName = targetName;
        this.targetRegex = targetRegex;
        this.targetValueCaptureGroup = targetValueCaptureGroup;
        compileRegexes();
    }

    private void compileRegexes() {
        try {
            if ( targetRegex != null ) {
                if ( RegexUtil.getMatchGroupCount(targetRegex) <= targetValueCaptureGroup ) {
                    this.targetRegexPattern = Pattern.compile(targetRegex);
                }
            }
        } catch ( PatternSyntaxException e ) {
            Logger.log("ERROR","Could not compile target regex");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetRegex() {
        return targetRegex;
    }

    public void setTargetRegex(String targetRegex) {
        this.targetRegex = targetRegex;
        compileRegexes();
    }

    public int getTargetValueCaptureGroup() {
        return targetValueCaptureGroup;
    }

    public void setTargetValueCaptureGroup(int targetValueCaptureGroup) {
        this.targetValueCaptureGroup = targetValueCaptureGroup;
    }

    public Pattern getTargetRegexPattern() {
        return targetRegexPattern;
    }

    public InjectorTargetExport toInjectorTargetExport() {
        InjectorTargetExport injectorTargetExport = new InjectorTargetExport();
        injectorTargetExport.id = id;
        injectorTargetExport.targetName = targetName;
        injectorTargetExport.targetRegex = targetRegex;
        injectorTargetExport.targetValueCaptureGroup = targetValueCaptureGroup;
        return injectorTargetExport;
    }
}
