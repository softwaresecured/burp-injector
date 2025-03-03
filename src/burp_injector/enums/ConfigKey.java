package burp_injector.enums;

public enum ConfigKey {
    INJECTOR_RULES,
    TEST_PAYLOAD,
    TEST_HTTP_REQUEST_MAP,
    TEST_HTTP_URL;

    public static final String KEY_PREFIX = "BurpInjector";

    public String resolve() {
        return KEY_PREFIX + name();
    }
}
