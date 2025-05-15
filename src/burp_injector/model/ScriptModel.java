package burp_injector.model;

import burp_injector.config.AbstractConfig;
import burp_injector.enums.ConfigKey;
import burp_injector.enums.ScriptOutputFile;
import burp_injector.enums.ScriptState;
import burp_injector.enums.ScriptType;
import burp_injector.event.model.ScriptModelEvent;
import burp_injector.model.data.ScriptExecutionResult;
import burp_injector.mvc.AbstractModel;
import burp_injector.python.PythonRuntime;
import burp_injector.python.PythonScript;
import burp_injector.util.Logger;

import java.util.ArrayList;

/**
 * Script model
 */
public class ScriptModel extends AbstractModel<ScriptModelEvent> {
    private RulesModel rulesModel;
    private RegexTargetsModel regexTargetsModel;
    private ScriptOutputFile scriptOutputFile = null;
    private ScriptType activeScriptTab = ScriptType.DECODE;
    private String lastStdout = null;
    private String lastStderr = null;
    private long lastExecutionTimeMs = 0;
    private String encodeScript = null;
    private String decodeScript = null;
    private String payloadProcessScript = null;
    private String payloadTest = null;
    private PythonRuntime pythonRuntime = PythonRuntime.getInstance();
    private ScriptState scriptState = ScriptState.NOT_RUNNING;
    private ScriptRunner scriptExecutionThread = null;

    public ScriptModel() {
        super();
        if ( pythonRuntime.getPythonPath() == null ) {
            emit(ScriptModelEvent.SCRIPT_CRITICAL_ERROR, null, "Could not detect python3 runtime");
        }
    }

    public RulesModel getRulesModel() {
        return rulesModel;
    }

    public void setRulesModel(RulesModel rulesModel) {
        this.rulesModel = rulesModel;
    }

    public RegexTargetsModel getRegexTargetsModel() {
        return regexTargetsModel;
    }

    public void setRegexTargetsModel(RegexTargetsModel regexTargetsModel) {
        this.regexTargetsModel = regexTargetsModel;
    }

    /*
        Config
    */
    @Override
    public void load(AbstractConfig config) {
        setPayloadTest(config.getString(ConfigKey.TEST_PAYLOAD, null));
    }

    @Override
    public void save(AbstractConfig config) {
        config.setString(ConfigKey.TEST_PAYLOAD, getPayloadTest());
    }

    /*
        Getters / Setters
     */

    public ScriptOutputFile getScriptOutputFile() {
        return scriptOutputFile;
    }

    public void setScriptOutputFile(ScriptOutputFile scriptOutputFile) {
        var old = this.scriptOutputFile;
        this.scriptOutputFile = scriptOutputFile;
        emit(ScriptModelEvent.SCRIPT_OUTPUT_FILE_CHANGED, old, scriptOutputFile);
    }

    public String getLastStdout() {
        return lastStdout;
    }

    public void setLastStdout(String lastStdout) {
        var old = this.lastStdout;
        this.lastStdout = lastStdout;
        emit(ScriptModelEvent.LAST_STDOUT_CHANGED, old, lastStdout);
    }

    public String getLastStderr() {
        return lastStderr;
    }

    public void setLastStderr(String lastStderr) {
        var old = this.lastStderr;
        this.lastStderr = lastStderr;
        emit(ScriptModelEvent.LAST_STDERR_CHANGED, old, lastStderr);
    }

    public long getLastExecutionTimeMs() {
        return lastExecutionTimeMs;
    }

    public void setLastExecutionTimeMs(long lastExecutionTimeMs) {
        var old = this.lastExecutionTimeMs;
        this.lastExecutionTimeMs = lastExecutionTimeMs;
        emit(ScriptModelEvent.LAST_EXECUTION_TIME_CHANGED, old, lastExecutionTimeMs);
    }

    public String getEncodeScript() {
        return encodeScript;
    }

    public void setEncodeScript(String encodeScript) {
        var old = this.encodeScript;
        this.encodeScript = encodeScript;
        emit(ScriptModelEvent.ENCODE_SCRIPT_CHANGED, old, encodeScript);
    }

    public String getDecodeScript() {
        return decodeScript;
    }

    public void setDecodeScript(String decodeScript) {
        var old = this.decodeScript;
        this.decodeScript = decodeScript;
        emit(ScriptModelEvent.DECODE_SCRIPT_CHANGED, old, decodeScript);
    }

    public String getPayloadProcessScript() {
        return payloadProcessScript;
    }

    public void setPayloadProcessScript(String payloadProcessScript) {
        var old = this.payloadProcessScript;
        this.payloadProcessScript = payloadProcessScript;
        emit(ScriptModelEvent.PAYLOAD_PROCESS_SCRIPT_CHANGED, old, payloadProcessScript);
    }

    public String getPayloadTest() {
        return payloadTest;
    }

    public void setPayloadTest(String payloadTest) {
        var old = this.payloadTest;
        this.payloadTest = payloadTest;
        emit(ScriptModelEvent.TEST_PAYLOAD_CHANGED, old, payloadTest);
    }

    public ScriptType getActiveScriptTab() {
        return activeScriptTab;
    }

    public void setActiveScriptTab(ScriptType activeScriptTab) {
        var old = this.activeScriptTab;
        this.activeScriptTab = activeScriptTab;
        emit(ScriptModelEvent.ACTIVE_SCRIPT_TAB_CHANGED, old, activeScriptTab);
    }

    public String getPythonPath() {
        return pythonRuntime.getPythonPath();
    }

    public void setPythonPath(String pythonPath) {
        var old = pythonRuntime.getPythonPath();
        pythonRuntime.setPythonPath(pythonPath);
        emit(ScriptModelEvent.PYTHON_PATH_CHANGED, old, pythonPath);
    }

    public ScriptState getScriptState() {
        return scriptState;
    }

    public void setScriptState(ScriptState scriptState) {
        var old = this.scriptState;
        this.scriptState = scriptState;
        emit(ScriptModelEvent.SCRIPT_STATE_CHANGED, old, scriptState);
    }

    /*
        Script logic
     */

    public void loadScript(ScriptType scriptType, String script) {
        switch ( scriptType ) {
            case ENCODE:
                setEncodeScript(script);
                break;
            case DECODE:
                setDecodeScript(script);
                break;
            case PAYLOAD:
                setPayloadProcessScript(script);
                break;
        }
        emit(ScriptModelEvent.SCRIPT_LOADED, null, scriptType);
    }

    private String getScriptInput() {
        String content = null;
        switch (getActiveScriptTab()) {
            case DECODE:
                content = getTargetAreaContent() == null ? "" : getTargetAreaContent();
                break;
            case ENCODE:
                content = getTargetAreaContent() == null ? "" : getTargetAreaContent();
                PythonScript pythonScript = new PythonScript(ScriptType.DECODE, getDecodeScript(), content);
                pythonScript.execute();
                setScriptOutputFile(ScriptOutputFile.STDOUT);
                if (pythonScript.hasErrors() ) {
                    setScriptOutputFile(ScriptOutputFile.STDERR);
                    setLastStderr(pythonScript.getStderr());
                }
                else {
                    content = pythonScript.getStdout();
                }
                break;
            case PAYLOAD:
                content = getPayloadTest() == null ? "" : getPayloadTest();
                break;
        }
        return content;
    }

    private String getTargetAreaContent() {
        return rulesModel.getTargetArea();
    }



    public void executeScript( ScriptType scriptType) {
        scriptExecutionThread = new ScriptRunner(scriptType,getScriptInput());
        Thread thread = new Thread(scriptExecutionThread);
        thread.start();
    }

    public void cancelScript() {
        if ( scriptExecutionThread != null ) {
            scriptExecutionThread.terminate();
            scriptExecutionThread = null;
        }
    }

    /*
        Threads
     */
    private class ScriptRunner implements Runnable {
        private ScriptType scriptType;
        private String scriptContent;
        private PythonScript pythonScript = null;

        public ScriptRunner( ScriptType scriptType, String scriptContent ) {
            this.scriptType = scriptType;
            this.scriptContent = scriptContent;
        }
        @Override
        public void run() {
            setScriptState(ScriptState.RUNNING);
            setLastStderr(null);
            setLastStdout(null);
            ScriptExecutionResult result = executeScript(scriptType,scriptContent);
            setLastStderr(result.stderr);
            setLastStdout(result.stdout);
            setScriptState(ScriptState.NOT_RUNNING);
            if( pythonScript.hasErrors() ) {
                setScriptOutputFile(ScriptOutputFile.STDERR);
            }
        }

        public void terminate() {
            if ( pythonScript != null ) {
                try {
                    pythonScript.terminate();
                    setScriptState(ScriptState.NOT_RUNNING);
                } catch (InterruptedException e) {
                    Logger.log("ERROR", String.format("Error while terminating script - %s", e.getMessage()));
                }
            }
        }

        public ScriptExecutionResult executeScript( ScriptType scriptType, String input ) {
            String scriptContent = null;
            switch ( scriptType ) {
                case ENCODE:
                    scriptContent = getEncodeScript();
                    break;
                case DECODE:
                    scriptContent = getDecodeScript();
                    break;
                case PAYLOAD:
                    scriptContent = getPayloadProcessScript();
                    break;
            }
            pythonScript = new PythonScript(scriptType, scriptContent, input);
            pythonScript.execute();
            setLastExecutionTimeMs(pythonScript.getExecutionTimeMs());
            ScriptExecutionResult result = new ScriptExecutionResult(pythonScript.getStderr(),pythonScript.getStdout());

            return result;
        }
    }

}
