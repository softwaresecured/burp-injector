import burp_injector.enums.ScriptType;
import burp_injector.python.PythonScript;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonScriptTests {
    @Test
    @DisplayName("Test python script execution")
    public void testPythonScript() {
        String testScript = """
                # payload processing script used to alter a payload prior to injection and encoding
                def process_payload( payload: str ) -> str:
                    return payload + "5678"
                """;
        PythonScript pythonScript = new PythonScript(ScriptType.PAYLOAD,testScript,"1234");
        pythonScript.execute();
        System.out.println(String.format("stdout = [%s]", pythonScript.getStdout()));
        System.out.println(String.format("stderr = [%s]", pythonScript.getStderr()));
        assertEquals("12345678", pythonScript.getStdout());
    }

    @Test
    @DisplayName("Test python script execution with exception")
    public void testPythonScriptException() {
        String testScript = """
                # payload processing script used to alter a payload prior to injection and encoding
                def process_payasdfload( payload: str ) -> str:
                    return payload + "5678"
                """;
        PythonScript pythonScript = new PythonScript(ScriptType.PAYLOAD,testScript,"1234");
        pythonScript.execute();
        assertTrue(pythonScript.getStderr().contains("is not defined"));
    }
}