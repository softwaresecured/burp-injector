package burp_injector.controller;

import burp_injector.enums.ScriptOutputFile;
import burp_injector.enums.ScriptType;
import burp_injector.event.controller.ScriptControllerEvent;
import burp_injector.exceptions.InjectorException;
import burp_injector.model.ScriptModel;
import burp_injector.mvc.AbstractController;
import burp_injector.python.PortableRuleScripts;
import burp_injector.util.Logger;
import burp_injector.util.MontoyaUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Script controller
 */
public class ScriptController  extends AbstractController<ScriptControllerEvent, ScriptModel>  {

    public ScriptController(ScriptModel model) {
        super(model);
    }
    private Component parentComponent = MontoyaUtil.getInstance().getApi().userInterface().swingUtils().suiteFrame();
    @Override
    protected void handleEvent(ScriptControllerEvent event, Object previous, Object next) {
        switch ( event ) {
            case DECODE_SCRIPT_UPDATED:
                getModel().setDecodeScript((String)next);
                getModel().setScriptOutputFile(ScriptOutputFile.STDOUT);
                getModel().setLastStdout("");
                getModel().setLastStderr("");
                break;
            case PAYLOAD_SCRIPT_UPDATED:
                getModel().setPayloadProcessScript((String)next);
                getModel().setScriptOutputFile(ScriptOutputFile.STDOUT);
                getModel().setLastStdout("");
                getModel().setLastStderr("");
                break;
            case ENCODE_SCRIPT_UPDATED:
                getModel().setEncodeScript((String)next);
                getModel().setScriptOutputFile(ScriptOutputFile.STDOUT);
                getModel().setLastStdout("");
                getModel().setLastStderr("");
                break;
            case TEST_PAYLOAD_UPDATED:
                getModel().setPayloadTest((String)next);
                break;
            case OUTPUT_FILE_STDERR_SELECTED:
                getModel().setScriptOutputFile(ScriptOutputFile.STDERR);
                break;
            case OUTPUT_FILE_STDOUT_SELECTED:
                getModel().setScriptOutputFile(ScriptOutputFile.STDOUT);
                break;
            case SCRIPT_EXPORT_CLICKED:
                if ( getModel().getPayloadTest() == null || getModel().getPayloadTest().isEmpty()) {
                    Logger.log("ERROR", "No payload");
                    JOptionPane.showMessageDialog(parentComponent,
                            "You must set a test payload",
                            "Could not export script",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;
                }
                if ( getModel().getRulesModel().getTargetArea() == null || getModel().getRulesModel().getTargetArea().isEmpty()) {
                    Logger.log("ERROR", "No target area");
                    JOptionPane.showMessageDialog(parentComponent,
                            "The target area content must not be null",
                            "Could not export script",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;
                }
                JFileChooser exportDialog = new JFileChooser();
                exportDialog.setMultiSelectionEnabled(false);
                exportDialog.setSelectedFile(new File(String.format("%s_scripts.py", getModel().getRulesModel().getRuleName())));
                if (exportDialog.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                    PortableRuleScripts portableRuleScripts = new PortableRuleScripts(
                            getModel().getPayloadProcessScript(),
                            getModel().getDecodeScript(),
                            getModel().getEncodeScript()
                    );
                    portableRuleScripts.setTestPayload(getModel().getPayloadTest());
                    portableRuleScripts.setTestTargetArea(getModel().getRulesModel().getTargetArea());
                    try {
                        Files.writeString(exportDialog.getSelectedFile().toPath(), portableRuleScripts.toScript());
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(parentComponent,
                                e.getMessage(),
                                "Could not export script",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
                break;
            case SCRIPT_IMPORT_CLICKED:
                JFileChooser importDialog = new JFileChooser();
                importDialog.setMultiSelectionEnabled(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Python Scripts", "py");
                importDialog.setFileFilter(filter);
                if (importDialog.showSaveDialog(MontoyaUtil.getInstance().getApi().userInterface().swingUtils().suiteFrame()) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = importDialog.getSelectedFile();
                        PortableRuleScripts portableRuleScripts = new PortableRuleScripts();
                        byte[] scriptBytes = Files.readAllBytes(Paths.get(file.toURI()));
                        portableRuleScripts.importScriptFile(new String(scriptBytes));
                        getModel().loadScript(ScriptType.ENCODE,portableRuleScripts.getEncodeScript());
                        getModel().loadScript(ScriptType.DECODE,portableRuleScripts.getDecodeScript());
                        getModel().loadScript(ScriptType.PAYLOAD,portableRuleScripts.getPayloadScript());
                    } catch (IOException|InjectorException e) {
                        JOptionPane.showMessageDialog(parentComponent,
                                e.getMessage(),
                                "Could not import script",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
                break;
            case SCRIPT_EXECUTE_TOGGLED:
                switch( getModel().getScriptState()) {
                    case RUNNING:
                        getModel().cancelScript();
                        break;
                    case NOT_RUNNING:
                        getModel().setScriptOutputFile(ScriptOutputFile.STDOUT);
                        getModel().executeScript(getModel().getActiveScriptTab());
                        break;
                }
                break;
            case ACTIVE_SCRIPT_TAB_UPDATED:
                getModel().setActiveScriptTab((ScriptType) next);
                break;
            case PYTHON_PATH_UPDATED:
                getModel().setPythonPath((String)next);
            default:
                Logger.log("ERROR", String.format("Unknown event %s received by %s", event.name(), this.getClass().getSimpleName()));
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        handleEvent(ScriptControllerEvent.valueOf(event.getPropertyName()), event.getOldValue(), event.getNewValue());
    }
}
