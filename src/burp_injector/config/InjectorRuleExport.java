package burp_injector.config;

import burp_injector.enums.TargetingMethod;

/**
 * Configuration export format for an individual injector rule
 * This can be serialized to JSON
 */
public class InjectorRuleExport {
    public String id;
    public String name = null;
    public String ruleScopeRegex = null;
    public String targetAreaRegex = null;
    public int targetAreaValueCaptureGroup = 1;
    public String ruleDescription = null;
    public boolean ruleEnabled = false;
    public TargetingMethod ruleTargetMethod = TargetingMethod.AUTO;
    public boolean autoTargetAutoGenerateName = false;
    public String customAutoTargetNameRegex = null;
    public String customAutoTargetRegex = null;
    public int customAutoTargetNameCaptureGroup = 0;
    public int customAutoTargetValueCaptureGroup = 0;

    // Scripts
    public String encodeScript = null;
    public String decodeScript = null;
    public String payloadProcessScript = null;

    public InjectorTargetExport[] targets = null;
}
