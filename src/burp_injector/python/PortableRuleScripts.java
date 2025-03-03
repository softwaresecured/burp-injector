package burp_injector.python;

import burp_injector.exceptions.InjectorException;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortableRuleScripts {
    private String encodeScript = null;
    private String decodeScript = null;
    private String payloadScript = null;
    private String testPayload = null;
    private String testTargetArea = null;

    public PortableRuleScripts() {

    }

    public PortableRuleScripts(String payloadScript, String decodeScript, String encodeScript) {
        this.payloadScript = payloadScript;
        this.decodeScript = decodeScript;
        this.encodeScript = encodeScript;
    }

    public String getEncodeScript() {
        return encodeScript;
    }

    public void setEncodeScript(String encodeScript) {
        this.encodeScript = encodeScript;
    }

    public String getDecodeScript() {
        return decodeScript;
    }

    public void setDecodeScript(String decodeScript) {
        this.decodeScript = decodeScript;
    }

    public String getPayloadScript() {
        return payloadScript;
    }

    public void setPayloadScript(String payloadScript) {
        this.payloadScript = payloadScript;
    }

    public String getTestPayload() {
        return testPayload;
    }

    public void setTestPayload(String testPayload) {
        this.testPayload = testPayload;
    }

    public String getTestTargetArea() {
        return testTargetArea;
    }

    public void setTestTargetArea(String testTargetArea) {
        this.testTargetArea = testTargetArea;
    }

    public String toScript() {
        return String.format("# Burp Injector development helper\n# Do not modify the structure of this script or change the function signatures\n# Do not remove the comments above and below the functions\n\nimport base64\nTARGET_AREA_CONTENT = base64.b64decode(\"%s\").decode(\"utf-8\") \nTEST_PAYLOAD_CONTENT = base64.b64decode(\"%s\").decode(\"utf-8\")\n\n\n# DECODE_FUNCTION_START\n\n%s\n    \n# DECODE_FUNCTION_END\n\n# ENCODE_FUNCTION_START\n\n%s\n    \n# ENCODE_FUNCTION_END\n\n\n# PAYLOAD_PROCESS_FUNCTION_START\n\n%s\n\n# PAYLOAD_PROCESS_FUNCTION_END\n\nif __name__ == '__main__':\n    decoded_target = decode_target(TARGET_AREA_CONTENT)\n    encoded_target = encode_target(decoded_target)\n    processed_payload = process_payload(TEST_PAYLOAD_CONTENT)\n    \n    print(f\"Decoded target:\\n{decoded_target}\\n\\nEncoded target:\\n{encoded_target}\\n\\nProcessed payload:\\n{processed_payload}\\n\")",
                Base64.getEncoder().encodeToString(testTargetArea.getBytes()),
                Base64.getEncoder().encodeToString(testPayload.getBytes()),
                getDecodeScript(),
                getEncodeScript(),
                getPayloadScript()
        );
    }

    public void importScriptFile(String script) throws InjectorException {
        String encodeScript = extractScript(script,"ENCODE");
        if ( encodeScript == null ) {
            throw new InjectorException("Could not extract encode_target function");
        }
        String decodeScript = extractScript(script,"DECODE");
        if ( encodeScript == null ) {
            throw new InjectorException("Could not extract decode_target function");
        }
        String payloadProcess = extractScript(script,"PAYLOAD_PROCESS");
        if ( encodeScript == null ) {
            throw new InjectorException("Could not extract process_payload function");
        }
        setEncodeScript(encodeScript);
        setDecodeScript(decodeScript);
        setPayloadScript(payloadProcess);
    }

    private String extractScript( String script, String functionBlock ) {
        String regexStr = String.format("#\\s+%s_FUNCTION_START(.*?)#\\s+%s_FUNCTION_END", functionBlock,functionBlock);
        Pattern p = Pattern.compile(regexStr,Pattern.DOTALL|Pattern.MULTILINE);
        Matcher m = p.matcher(script);
        if ( m.find() && m.groupCount() > 0 ) {
            return m.group(1).strip();
        }
        return null;
    }
}
