package burp_injector.python;

import burp_injector.constants.ScriptConstants;
import burp_injector.enums.ScriptType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
/**
 * A helper class to manage the execution of a python script
 */
public class PythonScript {
    private ScriptType scriptType;
    private String scriptContent;
    private String inputContent;
    private String stdout = null;
    private String stderr = null;
    private long executionTimeMs = 0;
    private PythonRuntime pythonRuntime = PythonRuntime.getInstance();
    private Process process = null;
    private String fullScript = null;

    /**
     * @param scriptType The type of script being run ( ENCODE, DECODE etc )
     * @param scriptContent The content of the script
     * @param inputContent The input value that will be embedded in the script as a variable
     */
    public PythonScript(ScriptType scriptType, String scriptContent, String inputContent) {
        this.scriptType = scriptType;
        this.scriptContent = scriptContent;
        this.inputContent = inputContent;
    }

    /**
     * Builds the python script based on the type of script executed
     * @return The full script or null if no valid script type was passed
     */
    private String buildScript() {
        String varHeader = String.format("_INPUT_CONTENT = \"%s\"\n", Base64.getEncoder().encodeToString(inputContent.getBytes()));
        switch ( scriptType ) {
            case ENCODE:
                return String.format("%s\n%s\n%s\n%s\n", varHeader, ScriptConstants.ENCODE_SCRIPT_HEADER,scriptContent,ScriptConstants.ENCODE_SCRIPT_FOOTER);
            case DECODE:
                return String.format("%s\n%s\n%s\n%s\n", varHeader, ScriptConstants.DECODE_SCRIPT_HEADER,scriptContent,ScriptConstants.DECODE_SCRIPT_FOOTER);
            case PAYLOAD:
                return String.format("%s\n%s\n%s\n%s\n", varHeader, ScriptConstants.PAYLOAD_PROCESS_SCRIPT_HEADER,scriptContent,ScriptConstants.PAYLOAD_PROCESS_SCRIPT_FOOTER);
        }
        return null;
    }

    /**
     * Executes the script and collects the stdout and stderr
     * If an exception is encountered running the script then the stderr will be set to the message of the exception
     */
    public void execute() {
        try {
            long startTime = System.currentTimeMillis();
            Path tempFile = Files.createTempFile("file", ".tmp");
            fullScript = buildScript();
            Files.write(tempFile, fullScript.getBytes("UTF-8"));

            ProcessBuilder pb = new ProcessBuilder(pythonRuntime.getPythonPath(),tempFile.toString());
            process = PythonScriptExecutorWatchdog.getInstance().startProcess(pb);
            stdout = new String(process.getInputStream().readAllBytes());
            stderr = new String(process.getErrorStream().readAllBytes());
            Files.delete(tempFile);
            executionTimeMs = System.currentTimeMillis()-startTime;
        } catch (Exception e) {
            stderr = e.getMessage();
        }

    }

    /**
     * Terminates a currently running process
     * @throws InterruptedException
     */
    public void terminate() throws InterruptedException {
        if ( process != null ) {
            process.destroyForcibly().waitFor();
            stderr = "terminated by user";
        }
    }

    /**
     * Returns the stdout of an executed process
     */
    public String getStdout() {
        if ( stdout != null ) {
            return stdout.strip();
        }
        return null;
    }

    /**
     * Returns the stderr of an executed process
     */
    public String getStderr() {
        if ( stderr != null ) {
            return stderr.strip();
        }
        return null;
    }

    /**
     * Returns the full script generated
     * @return
     */
    public String getFullScript() {
        return fullScript;
    }

    /**
     * Returns true if the stderr is set
     */
    public boolean hasErrors() {
        return getStderr() != null && !getStderr().isEmpty();
    }

    /**
     * Returns the execution time in ms
     */
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
}
