package burp_injector.editors;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp_injector.enums.ScriptType;
import burp_injector.event.model.RulesModelEvent;
import burp_injector.exceptions.InjectorException;
import burp_injector.model.RulesModel;
import burp_injector.model.data.InjectorRule;
import burp_injector.python.PythonScript;
import burp_injector.util.Logger;
import burp_injector.util.RequestUtil;
import burp_injector.util.UIUtil;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * The request editor provided to the repeater tab
 */
public class RequestEditor implements ExtensionProvidedHttpRequestEditor, PropertyChangeListener {
    private RulesModel rulesModel;
    private RequestEditorUI requestEditorUI = new RequestEditorUI();
    private ArrayList<InjectorRule> applicableRules = new ArrayList<InjectorRule>();
    private InjectorRule currentRule = null;
    private boolean updatingApplicableRules = false;
    //private HttpRequestResponse requestResponse = null;
    private HttpRequest baseRequest = null;
    public RequestEditor( RulesModel rulesModel) {
        this.rulesModel = rulesModel;
        this.rulesModel.addListener(this);
        initEventListeners();
    }

    public void setReadOnly() {
        requestEditorUI.jtxtDecodedTargetArea.setEditable(false);
    }

    public void initEventListeners() {
        requestEditorUI.jcmbApplicableRule.addActionListener(e -> {
            if ( !updatingApplicableRules ) {
                setRuleByName((String) requestEditorUI.jcmbApplicableRule.getSelectedItem());
                loadRequestContent();
            }
        });
    }

    public void setRuleByName( String name ) {
        for( InjectorRule rule : applicableRules ) {
            if ( rule.getName().equals(name)) {
                currentRule = rule;
                break;
            }
        }
    }

    @Override
    public HttpRequest getRequest() {
        HttpRequest modifiedRequest = baseRequest;
        if ( isModified() ) {
            String encodedTargetArea = processTargetArea(currentRule, ScriptType.ENCODE,requestEditorUI.getEditorContent());
            try {
                modifiedRequest = RequestUtil.rebuildRequest(
                        currentRule.getTargetAreaRegexPattern(),
                        currentRule.getTargetAreaValueCaptureGroup(),
                        baseRequest,
                        encodedTargetArea
                );
            } catch (InjectorException e) {
                Logger.log("ERROR", String.format("Error building request", e.getMessage()));
            }
        }
        return modifiedRequest;
    }

    private void setApplicableRules( ArrayList<InjectorRule> rules ) {
        updatingApplicableRules = true;
        applicableRules = rules;
        requestEditorUI.updateApplicableRules(applicableRules);
        if ( currentRule != null ) {
            if (!UIUtil.comboboxContains(requestEditorUI.jcmbApplicableRule,currentRule.getName())) {
                currentRule = null;
            }
        }
        updatingApplicableRules = false;
        if ( currentRule == null ) {
            if ( applicableRules.size() > 0 ) {
                setApplicableRule(applicableRules.getFirst());
            }
        }
        else {
            requestEditorUI.selectRuleByName(currentRule.getName());
        }
    }

    private void setApplicableRule( InjectorRule rule ) {
        currentRule = rule;
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        baseRequest = requestResponse.request();
        if ( currentRule == null ) {
            setApplicableRules(getApplicableRules(baseRequest));
        }
        loadRequestContent();
    }

    private void loadRequestContent() {
        if ( currentRule != null ) {
            Thread targetAreaProcessor = new Thread(
                new Runnable() {
                    public void run() {
                        requestEditorUI.jcmbApplicableRule.setEnabled(false);
                        requestEditorUI.jlblStatusMessage.setText("(Processing)");
                        String decodedTargetArea = getDecodedTargetArea(currentRule, baseRequest.toString());
                        requestEditorUI.setEditorContent(decodedTargetArea);
                        Logger.log("INFO", String.format("Using rule - %s", currentRule.getName()));
                        requestEditorUI.jcmbApplicableRule.setEnabled(true);
                    }
                });
            targetAreaProcessor.start();
        }
    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        if ( getApplicableRules(requestResponse.request()).size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String caption() {
        return "Injector";
    }

    @Override
    public Component uiComponent() {
        return requestEditorUI;
    }

    @Override
    public Selection selectedData() {
        return null;
    }

    @Override
    public boolean isModified() {
        return requestEditorUI.isChanged();
    }

    /**
     * Returns a list of injector rules that are capable of acting on a given request
     * @param request The base request / response that the repeater tab is working on
     * @return A list of injector rules that applies to the request or null if none applies
     */
    private ArrayList<InjectorRule> getApplicableRules(HttpRequest request) {
        ArrayList<InjectorRule> applicableRules = new ArrayList<InjectorRule>();
        for ( InjectorRule rule : rulesModel.getInjectorRules() ) {
            if ( rule.isApplicable(request)) {
                applicableRules.add(rule);
            }
        }
        Logger.log("INFO", String.format("applicableRules is %d", applicableRules.size()));
        return applicableRules;
    }


    /**
     * Returns the decoded target area decoded by the decoder script
     * @param rule The injector rule
     * @param baseRequest The base HTTP request in string form that contains the target area
     * @return
     */
    private String getDecodedTargetArea( InjectorRule rule, String baseRequest ) {
        String decodedTargetArea = null;
        Matcher targetAreaMatcher = rule.getTargetAreaRegexPattern().matcher(baseRequest);
        if ( targetAreaMatcher.find()) {
            if ( targetAreaMatcher.groupCount() <= rule.getTargetAreaValueCaptureGroup()) {
                decodedTargetArea = processTargetArea(
                        rule,
                        ScriptType.DECODE,
                        targetAreaMatcher.group(rule.getTargetAreaValueCaptureGroup())
                );
            }
        }
        return decodedTargetArea;
    }

    /**
     * Run the script defined in the rule for a given target content
     * @param rule The injector rule containing the script
     * @param scriptType The script type
     * @param targetContent The target content that the script will process
     * @return
     */
    private String processTargetArea(InjectorRule rule, ScriptType scriptType, String targetContent ) {
        String decodedTargetArea = null;
        String scriptContent = rule.getDecodeScript();
        if ( scriptType.equals(ScriptType.ENCODE)) {
            scriptContent = rule.getEncodeScript();
        }
        PythonScript pythonScript = new PythonScript(scriptType, scriptContent, targetContent);
        pythonScript.execute();
        if ( !pythonScript.hasErrors() ) {
            requestEditorUI.jlblStatusMessage.setText("");
            decodedTargetArea = pythonScript.getStdout();
        }
        else {
            requestEditorUI.jlblStatusMessage.setText("(Error)");
            Logger.log("ERROR", String.format("Error processing payload in RequestEditor - %s", pythonScript.getStderr()));
        }
        return decodedTargetArea;
    }

    protected void handleEvent(RulesModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case RULE_DELETED:
            case RULE_SAVED:
                Logger.log("DEBUG","Updating applicable rules");
                setApplicableRules(getApplicableRules(baseRequest));
                break;
            default:
                Logger.log("ERROR", String.format("Unknown event %s received by %s", event.name(), this.getClass().getSimpleName()));
        }
    }
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(RulesModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }
}
