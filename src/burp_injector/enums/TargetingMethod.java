package burp_injector.enums;

public enum TargetingMethod {
    AUTO,           // Best effort auto targeting using built in regexes
    CUSTOM_AUTO,    // Custom auto targeting using user provided regex
    REGEX           // Specific targeting using user provided regex and user provided name
}
