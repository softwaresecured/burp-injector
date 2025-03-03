package burp_injector.insertionpoint;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Range;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint;
import burp_injector.enums.ScriptType;
import burp_injector.exceptions.InjectorException;
import burp_injector.python.PythonScript;
import burp_injector.util.Logger;
import burp_injector.util.MontoyaUtil;
import burp_injector.util.RequestUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class InjectorInsertionPoint implements AuditInsertionPoint {

    private HttpRequest baseRequest; // The base request provided by the scanner
    private String encodeScript; // The script used to encode the payload
    private String decodeScript; // The script used to decode the payload
    private String payloadProcessScript; // the script used to process the payload before insertion

    private Pattern targetAreaRegex = null; // The target area regex
    private int targetAreaCaptureGroup = -1; // The target area regex capture group


    // If insertionTargetName is null then the nameRegex and nameCaptureGroup are used to obtain the name
    private String injectorRuleName = null;
    private String insertionTargetName = null;
    private Pattern targetRegex = null;
    private int nameCaptureGroup = -1;
    private int targetCaptureGroup = -1;
    private String decodedTargetArea = null;

    private int matchNumber = 0;

    // Scripts
    private PythonScript payloadProcessorPythonScript = null;
    private PythonScript encodeScriptPythonScript = null;
    private PythonScript decodeScriptPythonScript = null;

    boolean aborted = false;

    public InjectorInsertionPoint(
            HttpRequest baseRequest,
            String encodeScript,
            String decodeScript,
            String payloadProcessScript,
            Pattern targetAreaRegex,
            int targetAreaCaptureGroup,
            String injectorRuleName,
            String insertionTargetName,
            Pattern targetRegex,
            int nameCaptureGroup,
            int targetCaptureGroup,
            int matchNumber
    ) throws InsertionPointException {
        this.baseRequest = baseRequest;
        this.encodeScript = encodeScript;
        this.decodeScript = decodeScript;
        this.payloadProcessScript = payloadProcessScript;
        this.targetAreaRegex = targetAreaRegex;
        this.targetAreaCaptureGroup = targetAreaCaptureGroup;
        this.injectorRuleName = injectorRuleName;
        this.insertionTargetName = insertionTargetName;
        this.targetRegex = targetRegex;
        this.nameCaptureGroup = nameCaptureGroup;
        this.targetCaptureGroup = targetCaptureGroup;
        this.matchNumber = matchNumber;
    }



    public void init() throws InsertionPointException {
        decodeTargetArea();
    }

    public void abort() {
        aborted = true;
        terminateScript(decodeScriptPythonScript);
        terminateScript(payloadProcessorPythonScript);
        terminateScript(encodeScriptPythonScript);
    }

    private void terminateScript ( PythonScript pythonScript ) {
        if ( pythonScript != null ) {
            try {
                pythonScript.terminate();
            } catch (InterruptedException e) {
                ;
            }
        }
    }

    private String extractTargetArea() {
        Matcher m = targetAreaRegex.matcher(baseRequest.toString());
        if ( m.find()  ) {
            if ( m.groupCount() >= targetAreaCaptureGroup ) {
                return m.group(targetAreaCaptureGroup);
            }
        }
        return null;
    }

    private void decodeTargetArea() throws InsertionPointException {
        String targetArea = extractTargetArea();
        if ( targetArea != null ) {
            decodeScriptPythonScript = new PythonScript(ScriptType.DECODE, decodeScript, targetArea);
            decodeScriptPythonScript.execute();
            if ( !decodeScriptPythonScript.hasErrors()) {
                decodedTargetArea = decodeScriptPythonScript.getStdout();
            }
        }
    }


    /*
        Injector logic
     */

    private String getInjectorTargetName() {
        if ( insertionTargetName != null ) {
            return insertionTargetName;
        }
        else {
            if ( nameCaptureGroup == -1 ) {
                return String.format("parameter_%d", matchNumber);
            }
            else {
                Matcher m = targetRegex.matcher(decodedTargetArea);
                if ( m.find() && m.groupCount() >= nameCaptureGroup ) {
                    return m.group(nameCaptureGroup);
                }
            }
        }
        return null;
    }

    private String buildInjectorTargetFullName() {
        String injectorTargetName = getInjectorTargetName();
        if ( injectorTargetName != null ) {
            return String.format("%s/%s", injectorRuleName,injectorTargetName);
        }
        return injectorRuleName;
    }



    private String processPayload( ByteArray payload ) {
        payloadProcessorPythonScript = new PythonScript(ScriptType.PAYLOAD, payloadProcessScript, new String(payload.getBytes(),StandardCharsets.UTF_8));
        payloadProcessorPythonScript.execute();
        if ( !payloadProcessorPythonScript.hasErrors()) {
            return payloadProcessorPythonScript.getStdout();
        }
        return null;
    }

    /*
            Burp API
    */
    @Override
    public String name() {
        return buildInjectorTargetFullName();
    }

    @Override
    public String baseValue() {
        Matcher m = targetRegex.matcher(decodedTargetArea);
        int i = 1;
        while (m.find()) {
            if ( i == matchNumber ) {
                if ( m.groupCount() >= targetCaptureGroup ) {
                    Logger.log("DEBUG",String.format("baseValue = %s", m.group(targetCaptureGroup)));
                    return m.group(targetCaptureGroup);
                }
            }
            i++;
        }
        return null;
    }

    @Override
    public HttpRequest buildHttpRequestWithPayload(ByteArray payload) {
        Logger.log("DEBUG","buildHttpRequestWithPayload");
        String processedPayload = processPayload(payload);
        if ( processedPayload != null) {
            Matcher m = targetRegex.matcher(decodedTargetArea);
            int i = 1;
            while ( m.find() && !aborted ) {
                if ( i == matchNumber ) {
                    if ( m.groupCount() >= targetCaptureGroup ) {
                        String processedTargetArea = String.format(
                                "%s%s%s",
                                decodedTargetArea.substring(0,m.start(targetCaptureGroup)),
                                processedPayload,
                                decodedTargetArea.substring(m.end(targetCaptureGroup))
                        );
                        encodeScriptPythonScript = new PythonScript(ScriptType.ENCODE, encodeScript, processedTargetArea);
                        encodeScriptPythonScript.execute();
                        if ( !encodeScriptPythonScript.hasErrors() ) {
                            HttpRequest rebuiltRequest = null;
                            try {
                                rebuiltRequest = RequestUtil.rebuildRequest(targetAreaRegex,targetAreaCaptureGroup,baseRequest,encodeScriptPythonScript.getStdout());
                            } catch (InjectorException e) {
                                Logger.log("DEBUG",String.format("buildHttpRequestWithPayload - exception %s", e.getMessage()));
                            }
                            if ( rebuiltRequest == null ) {
                                Logger.log("DEBUG","buildHttpRequestWithPayload - rebuilt request is null");
                            }
                            Logger.log("DEBUG","buildHttpRequestWithPayload - returning rebuilt request");
                            return rebuiltRequest;
                        }
                        else {
                            Logger.log("DEBUG",String.format("buildHttpRequestWithPayload - pythonScript.hasErrors() %s", encodeScriptPythonScript.getStderr()));
                            Logger.log("DEBUG",String.format("buildHttpRequestWithPayload - script [%s]", encodeScriptPythonScript.getFullScript()));
                            Logger.log("DEBUG",String.format("buildHttpRequestWithPayload - script [%s]", processedTargetArea));
                        }
                    }
                    else {
                        Logger.log("DEBUG","buildHttpRequestWithPayload - capture group error");
                    }
                }
                i++;
            }
        }
        return null;
    }

    @Override
    public List<Range> issueHighlights(ByteArray payload) {
        List<Range> ranges = new ArrayList<Range>();
        ranges.add(Range.range(0,0));
        return ranges;
    }


}
