package burp_injector.event.model;

public enum ScriptModelEvent {
    LAST_STDOUT_CHANGED,
    LAST_STDERR_CHANGED,
    SCRIPT_OUTPUT_FILE_CHANGED,
    LAST_EXECUTION_TIME_CHANGED,
    ENCODE_SCRIPT_CHANGED,
    DECODE_SCRIPT_CHANGED,
    PAYLOAD_PROCESS_SCRIPT_CHANGED,
    TEST_PAYLOAD_CHANGED,
    ACTIVE_SCRIPT_TAB_CHANGED,
    SCRIPT_LOADED,
    PYTHON_PATH_CHANGED,
    SCRIPT_CRITICAL_ERROR,
    SCRIPT_STATE_CHANGED,
    TARGET_AREA_CONTENT_CHANGED
}
