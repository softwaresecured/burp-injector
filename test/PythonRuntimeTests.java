import burp_injector.python.PythonRuntime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PythonRuntimeTests {
    @Test
    @DisplayName("Test singleton")
    public void detectPython3Test() {
        PythonRuntime pythonRuntime = PythonRuntime.getInstance();
        System.out.println(String.format("Python path = %s", pythonRuntime.getPythonPath()));
        assertTrue(pythonRuntime.getPythonPath()!=null);
    }
}