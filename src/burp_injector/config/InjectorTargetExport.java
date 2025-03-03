package burp_injector.config;

/**
 * Configuration export format for an injector target
 * This can be serialized to JSON
 */
public class InjectorTargetExport {
    public String id;
    public String targetName;
    public String targetRegex;
    public int targetValueCaptureGroup;
}
