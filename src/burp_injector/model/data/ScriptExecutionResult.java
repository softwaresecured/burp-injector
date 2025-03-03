package burp_injector.model.data;

/**
 * The result of a python script execution
 */
public class ScriptExecutionResult {
    public String stderr = null;
    public String stdout = null;

    public ScriptExecutionResult(String stderr, String stdout) {
        this.stderr = stderr;
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }
}
