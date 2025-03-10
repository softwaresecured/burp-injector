package burp_injector.event.model;

public enum RulesModelEvent {
    RULE_STACK_CHANGED,
    CUSTOM_AUTO_TARGET_NAME_AUTOGENERATE_CHANGED,
    CUSTOM_AUTO_TARGET_VALUE_CAPTURE_GROUP_CHANGED,
    CUSTOM_AUTO_TARGET_NAME_CAPTURE_GROUP_CHANGED,
    CUSTOM_AUTO_TARGET_NAME_REGEX_CHANGED,
    CUSTOM_AUTO_TARGET_VALUE_REGEX_CHANGED,
    RULE_TARGETING_METHOD_CHANGED,
    RULE_ENABLED_CHANGED,
    TARGET_AREA_CAPTURE_GROUP_CHANGED,
    TARGET_AREA_REGEX_CHANGED,
    RULE_DESCRIPTION_CHANGED,
    RULE_SCOPE_CHANGED,
    RULE_NAME_CHANGED,
    RULE_ID_CHANGED,
    LAST_RULE_ID_CHANGED,
    RULE_SAVED,
    RULE_DELETED,
    LAST_ERROR_ALERT_CHANGED,
    RULES_LOADED,
    TEST_STATE_CHANGED,
    TEST_TOTAL_TASKS_CHANGED,
    TEST_CURRENT_TASK_CHANGED,
    RULE_EDITOR_STATE_CHANGED
}
