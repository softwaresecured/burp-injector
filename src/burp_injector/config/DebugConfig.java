package burp_injector.config;

import burp_injector.python.PythonScriptExecutorWatchdog;
import burp_injector.ui.DebugEditorDialog;
import burp_injector.util.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * The debug config editor is activated by entering the Konami code
 * This is done by typing the super secret code on the script output jtextarea
 */
public final class DebugConfig {
    private static DebugEditorDialog debugEditorDialog = new DebugEditorDialog();
    private boolean debugEnabled = false;
    private boolean perfTraceEnabled = false;

    private static int[] konamiCode = {38, 38, 40, 40, 37, 39, 37, 39, 66, 65, 10}; // ↑ ↑ ↓ ↓ ← → ← → b a
    ArrayList<Character> keyBuffer = new ArrayList<Character>();
    private static DebugConfig INSTANCE;
    private static HashMap<String, Callable> debugFunctions = new HashMap<>();
    private static String[] arguments = {};

    public DebugConfig() {
        debugFunctions.put("set", this::_debugSet);
    }

    public static DebugConfig getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DebugConfig();
        }
        return INSTANCE;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isPerfTraceEnabled() {
        return perfTraceEnabled;
    }

    public void setPerfTraceEnabled(boolean perfTraceEnabled) {
        this.perfTraceEnabled = perfTraceEnabled;
    }

    public void attachTextArea(JTextArea jTextArea ) {
        jTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyBuffer.add((char) e.getKeyCode());
                if ( keyBuffer.size() > 11 ) {
                    keyBuffer.removeFirst();
                }
                if ( checkDebugActivation(keyBuffer)) {
                    enableDebugEditor();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public static boolean checkDebugActivation(ArrayList<Character> keyBuffer) {
        if ( keyBuffer.size() == konamiCode.length ) {
            for ( int i = 0 ; i < keyBuffer.size(); i++ ) {
                if (keyBuffer.get(i) != konamiCode[i] ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void enableDebugEditor() {
        Logger.log("INFO","(∩ ͡° ͜ʖ ͡°)⊃━☆ﾟ. * Debug Configuration Enabled! Be careful!");
        debugEditorDialog.setVisible(true);
        applyConfiguration(debugEditorDialog.getDebugConfigParams());
    }

    public static void applyConfiguration( String config ) {
        for ( String line : config.split("\n")) {
            String commandParts[] = line.split(" ");
            Callable fn = debugFunctions.get(commandParts[0]);
            if ( fn != null ) {
                try {
                    if ( commandParts.length > 1 ) {
                        arguments = new String[commandParts.length-1];
                        for ( int i = 1; i < commandParts.length; i++ ) {
                            arguments[i-1] = commandParts[i];
                        }
                    }
                    fn.call();
                    arguments = new String[]{};
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /*
        Config commands
    */
    // Sets the value of things like debug toggle, perf toggle, watchdog timeout
    private int _debugSet() {
        if ( arguments.length == 2 ) {
            switch ( arguments[0]) {
                case "watchdog_timeout_sec":
                    if ( arguments[1].matches("\\d+")) {
                        PythonScriptExecutorWatchdog.getInstance().setMaxProcessExecutionTimeSec(Integer.parseInt(arguments[1]));
                    }
                    break;
                case "debug_logging":
                    if ( arguments[1].matches("(true|false)")) {
                        setDebugEnabled(arguments[1].matches("true") ? true : false );
                    }
                    break;
            }
        }
        return 0;
    }

    // Logs various things to the debug log
    private int _debugDump() {
        Logger.log("INFO","Running debug set");
        return 0;
    }
}
