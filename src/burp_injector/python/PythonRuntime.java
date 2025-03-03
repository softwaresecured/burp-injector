package burp_injector.python;

import java.io.IOException;

/**
 * A singleton class that can be used to gain access to a python interpreter
 */
public final class PythonRuntime {
    private String POTENTIAL_PYTHON_3_LOCATIONS[] = {
            "python3",
            "python",
            "python.exe",
            "/usr/bin/python3",
            "/opt/homebrew/bin/python3",
            "/usr/local/bin/python3"
    };
    private static PythonRuntime INSTANCE;
    private String pythonPath = null;

    public PythonRuntime() {
        detectPython3();
    }

    public static PythonRuntime getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PythonRuntime();
        }

        return INSTANCE;
    }

    /**
     * Returns the path to the python interpreter
     * @return The full path of the python interpreter
     */
    public String getPythonPath() {
        return pythonPath;
    }

    /**
     * Sets the path to the python interpreter
     * @param pythonPath
     */
    public void setPythonPath(String pythonPath) {
        this.pythonPath = pythonPath;
    }

    /**
     * Attempts to run python from various locations starting with the current path
     * On every execution the version is checked. If the version is python3, then the path is returned so that the
     * full path may be set
     */
    private void detectPython3() {
        // Try what is in our path
        for ( String pythonName : POTENTIAL_PYTHON_3_LOCATIONS ) {
            try {
                String version = getOutput(pythonName,pythonName, "-c","from sys import version_info as python_version_info;print(python_version_info.major)");
                if ( version.startsWith("3")) {
                    String fullPath = getOutput(pythonName,pythonName, "-c","import sys;print(sys.executable)");
                    pythonPath = fullPath;
                }
            } catch (IOException e) {
                ;
            }
        }
    }

    /**
     * Gets the output from a command invoked via ProcessBuilder
     * @param pythonPath The command to run
     * @param command The command arguments
     * @return The stdout of the command that was run
     * @throws IOException
     */
    private String getOutput( String pythonPath, String... command ) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        return new String(process.getInputStream().readAllBytes()).strip();
    }
}