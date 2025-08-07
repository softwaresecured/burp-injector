package burp_injector.view;

import burp_injector.config.DebugConfig;
import burp_injector.enums.EditorState;
import burp_injector.enums.ScriptState;
import burp_injector.enums.ScriptType;
import burp_injector.event.controller.ScriptControllerEvent;
import burp_injector.event.model.RegexTargetsModelEvent;
import burp_injector.event.model.RulesModelEvent;
import burp_injector.event.model.ScriptModelEvent;
import burp_injector.model.RegexTargetsModel;
import burp_injector.model.RulesModel;
import burp_injector.model.ScriptModel;
import burp_injector.mvc.AbstractView;
import burp_injector.targeting.AutoTargeter;
import burp_injector.ui.PnlScriptOutput;
import burp_injector.ui.PnlScriptPanel;
import burp_injector.util.Logger;
import burp_injector.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Script view
 */
public class ScriptView extends AbstractView<ScriptControllerEvent, ScriptModel, ScriptModelEvent> {
    public final JTabbedPane jTabbedPaneScripts = new JTabbedPane();
    public final PnlScriptPanel pnlEncodeScript = new PnlScriptPanel();
    public final PnlScriptPanel pnlDecodeScript = new PnlScriptPanel();
    public final PnlScriptPanel pnlPayloadProcess = new PnlScriptPanel();
    public PnlScriptOutput pnlScriptOutput = new PnlScriptOutput();
    private Component parentComponent;
    public ScriptView(ScriptModel model) {
        super(model);
        setEnabled(false);
        pnlScriptOutput.jtxtPythonPath.setText(getModel().getPythonPath());
    }

    @Override
    public void attachListeners() {
        attach(pnlEncodeScript.script, ScriptControllerEvent.ENCODE_SCRIPT_UPDATED);
        attach(pnlDecodeScript.script,ScriptControllerEvent.DECODE_SCRIPT_UPDATED);
        attach(pnlPayloadProcess.script,ScriptControllerEvent.PAYLOAD_SCRIPT_UPDATED);
        attach(pnlScriptOutput.jtxtSamplePayload,ScriptControllerEvent.TEST_PAYLOAD_UPDATED);
        attach(pnlScriptOutput.jtxtPythonPath,ScriptControllerEvent.PYTHON_PATH_UPDATED);
        attach(pnlScriptOutput.jRadioButtonStderr,ScriptControllerEvent.OUTPUT_FILE_STDERR_SELECTED);
        attach(pnlScriptOutput.jRadioButtonStdout,ScriptControllerEvent.OUTPUT_FILE_STDOUT_SELECTED);
        attach(pnlScriptOutput.jbtnImportScripts,ScriptControllerEvent.SCRIPT_IMPORT_CLICKED);
        attach(pnlScriptOutput.jbtnExportScripts,ScriptControllerEvent.SCRIPT_EXPORT_CLICKED);
        attach(pnlScriptOutput.jbtnExecuteToggle,ScriptControllerEvent.SCRIPT_EXECUTE_TOGGLED);
        attach(jTabbedPaneScripts,ScriptControllerEvent.ACTIVE_SCRIPT_TAB_UPDATED);
        // Attach the debug editor to the output of the script tab
        DebugConfig.getInstance().attachTextArea(pnlScriptOutput.jtxtOutput);
    }

    private void setEnabled( boolean status) {
        pnlDecodeScript.setEnabled(status);
        pnlEncodeScript.setEnabled(status);
        pnlPayloadProcess.setEnabled(status);
        pnlScriptOutput.setEnabled(status);
        jTabbedPaneScripts.setEnabled(status);
    }

    @Override
    protected void handleEvent(ScriptModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case LAST_EXECUTION_TIME_CHANGED:
                pnlScriptOutput.jlblExecutionTimeMs.setText(String.format("Execution time: %d ms", getModel().getLastExecutionTimeMs()));
                break;
            case LAST_STDOUT_CHANGED:
            case LAST_STDERR_CHANGED:
                switch ( getModel().getScriptOutputFile() ) {
                    case STDERR:
                        pnlScriptOutput.jtxtOutput.setText(getModel().getLastStderr());
                        break;
                    case STDOUT:
                        pnlScriptOutput.jtxtOutput.setText(getModel().getLastStdout());
                        updateTargetHighlighting();
                        break;
                }
                break;
            case SCRIPT_OUTPUT_FILE_CHANGED:
                switch ( getModel().getScriptOutputFile() ) {
                    case STDERR:
                        pnlScriptOutput.jtxtOutput.setText(getModel().getLastStderr());
                        pnlScriptOutput.outputFileRadioButtonGroup.setSelected(pnlScriptOutput.jRadioButtonStderr.getModel(),true);
                        break;
                    case STDOUT:
                        pnlScriptOutput.jtxtOutput.setText(getModel().getLastStdout());
                        pnlScriptOutput.outputFileRadioButtonGroup.setSelected(pnlScriptOutput.jRadioButtonStdout.getModel(),true);
                        break;
                }
                break;
            case SCRIPT_LOADED:
                switch ((ScriptType)next) {
                    case ENCODE:
                        pnlEncodeScript.script.setText(getModel().getEncodeScript());
                        break;
                    case DECODE:
                        pnlDecodeScript.script.setText(getModel().getDecodeScript());
                        break;
                    case PAYLOAD:
                        pnlPayloadProcess.script.setText(getModel().getPayloadProcessScript());
                        break;
                }
                break;
            case RESET:
                pnlScriptOutput.jtxtOutput.setText("");
                pnlScriptOutput.jlblExecutionTimeMs.setText("0 ms");
                pnlEncodeScript.script.setText("");
                pnlDecodeScript.script.setText("");
                pnlPayloadProcess.script.setText("");
                pnlScriptOutput.jtxtSamplePayload.setText("");
                break;
            case SCRIPT_CRITICAL_ERROR:
                JOptionPane.showMessageDialog(parentComponent, (String) next, "Python runtime not available", JOptionPane.ERROR_MESSAGE);
                break;
            case SCRIPT_STATE_CHANGED:
                switch((ScriptState)next){
                    case RUNNING:
                        pnlScriptOutput.jbtnExecuteToggle.setText("Cancel");
                        break;
                    case NOT_RUNNING:
                        pnlScriptOutput.jbtnExecuteToggle.setText("Execute");
                        break;
                }
                break;
            case ACTIVE_SCRIPT_TAB_CHANGED:
                ScriptType selectedTab = (ScriptType)next;
                if ( selectedTab != null ) {
                    for ( int i = 0; i < jTabbedPaneScripts.getTabCount(); i++ ) {
                        if ( selectedTab.toString().startsWith(jTabbedPaneScripts.getTitleAt(i).toUpperCase())) {
                            jTabbedPaneScripts.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                break;
        }
    }

    protected void handleEvent(RulesModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case RULE_EDITOR_STATE_CHANGED:
                switch ((EditorState)next) {
                    case CREATE:
                    case EDIT:
                        setEnabled(true);
                        break;
                    case INITIAL:
                    case DISABLED:
                        setEnabled(false);
                        break;
                }
                break;
            case CUSTOM_AUTO_TARGET_NAME_AUTOGENERATE_CHANGED:
            case CUSTOM_AUTO_TARGET_NAME_CAPTURE_GROUP_CHANGED:
            case CUSTOM_AUTO_TARGET_VALUE_CAPTURE_GROUP_CHANGED:
            case CUSTOM_AUTO_TARGET_VALUE_REGEX_CHANGED:
                updateTargetHighlighting();
                break;
            default:
                Logger.log("ERROR", String.format("Unknown event %s received by %s", event.name(), this.getClass().getSimpleName()));
        }
    }

    protected void handleEvent(RegexTargetsModelEvent event, Object previous, Object next) {
        switch (event) {
            case TARGET_CAPTURE_GROUP_CHANGED:
            case TARGET_REGEX_CHANGED:
                updateRegexTargetMethodHighlighting();
                break;
            default:
                Logger.log("ERROR", String.format("Unknown event %s received by %s", event.name(), this.getClass().getSimpleName()));
        }
    }

    private void updateTargetHighlighting() {
        switch (getModel().getRulesModel().getRuleTargetingMethod()) {
            case AUTO:
                AutoTargeter autoTargeter = new AutoTargeter(getModel().getLastStdout());
                if ( autoTargeter.getTargetPattern() != null ) {
                    updateAutoTargetRegexMethodHighlighting(
                            autoTargeter.getTargetPattern().toString(),
                            -1,
                            autoTargeter.getValueCaptureGroup()
                    );
                }
                break;
            case CUSTOM_AUTO:
                updateAutoTargetRegexMethodHighlighting(
                        getModel().getRulesModel().getCustomAutoTargetRegex(),
                        getModel().getRulesModel().getCustomAutoTargetNameCaptureGroup(),
                        getModel().getRulesModel().getCustomAutoTargetValueCaptureGroup()
                );
                break;
            case REGEX:
                updateRegexTargetMethodHighlighting();
                break;
            default:
                Logger.log("DEBUG", String.format("Unknown targeting method %s",getModel().getRulesModel().getRuleTargetingMethod().name()));
        }
    }

    private void updateAutoTargetRegexMethodHighlighting(String regex, int nameCaptureGroup, int valueCaptureGroup) {
        if ( getModel().getActiveScriptTab().equals(ScriptType.DECODE)) {
            pnlScriptOutput.jtxtOutput.getHighlighter().removeAllHighlights();
            // Name
            if ( nameCaptureGroup > 0 ) {
                UIUtil.updateHighlighting(
                        regex,
                        nameCaptureGroup,
                        pnlScriptOutput.jtxtOutput,
                        Color.blue
                );
            }
            // Value
            UIUtil.updateHighlighting(
                    regex,
                    valueCaptureGroup,
                    pnlScriptOutput.jtxtOutput,
                    new Color(93, 63, 211)
            );
        }
    }

    private void updateRegexTargetMethodHighlighting() {
        if ( getModel().getActiveScriptTab().equals(ScriptType.DECODE)) {
            pnlScriptOutput.jtxtOutput.getHighlighter().removeAllHighlights();
            UIUtil.updateHighlighting(
                    getModel().getRegexTargetsModel().getTargetRegex(),
                    getModel().getRegexTargetsModel().getTargetCaptureGroup(),
                    pnlScriptOutput.jtxtOutput
            );
        }
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() instanceof RulesModel ) {
            handleEvent(RulesModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
        if ( evt.getSource() instanceof ScriptModel ) {
            handleEvent(ScriptModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }

        if ( evt.getSource() instanceof RegexTargetsModel) {
            handleEvent(RegexTargetsModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
    }
}
