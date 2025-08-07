package burp_injector.view;

import burp_injector.enums.EditorState;
import burp_injector.enums.TargetingMethod;
import burp_injector.event.controller.RulesControllerEvent;
import burp_injector.event.model.RulesModelEvent;
import burp_injector.model.RulesModel;
import burp_injector.mvc.AbstractView;
import burp_injector.ui.JfrmTestRunnerOutput;
import burp_injector.ui.PnlInjectorRuleEditor;
import burp_injector.ui.PnlInjectorRules;
import burp_injector.util.Logger;
import burp_injector.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Rules view
 */

public class RulesView extends AbstractView<RulesControllerEvent, RulesModel, RulesModelEvent> {
    public PnlInjectorRules pnlInjectorRules;
    public PnlInjectorRuleEditor pnlInjectorRuleEditor;
    public JfrmTestRunnerOutput jfrmTestRunnerOutput = new JfrmTestRunnerOutput();
    private Component parentComponent = null;
    public RulesView(RulesModel model) {
        super(model);
        pnlInjectorRules = new PnlInjectorRules(model);
        pnlInjectorRuleEditor = new PnlInjectorRuleEditor(model);
        pnlInjectorRuleEditor.setEnabled(false);
    }

    @Override
    public void attachListeners() {
        attach(pnlInjectorRules.jbtnNew,RulesControllerEvent.NEW);
        attach(pnlInjectorRules.jbtnSave,RulesControllerEvent.SAVE);
        attach(pnlInjectorRules.jbtnDelete,RulesControllerEvent.DELETE);
        attach(pnlInjectorRules.jbtnCancel,RulesControllerEvent.CANCEL);
        attach(pnlInjectorRules.jbtnTest,RulesControllerEvent.TEST);
        attach(pnlInjectorRuleEditor.jtxtRuleName,RulesControllerEvent.NAME_UPDATED);
        attach(pnlInjectorRuleEditor.jtxtRuleDescription,RulesControllerEvent.DESCRIPTION_UPDATED);
        attach(pnlInjectorRuleEditor.jtxtRuleScope,RulesControllerEvent.SCOPE_UPDATED);
        attach(pnlInjectorRuleEditor.jtxtTargetAreaRegex,RulesControllerEvent.TARGET_AREA_REGEX_UPDATED);
        attach(pnlInjectorRuleEditor.jspnTargetAreaCaptureGroup,RulesControllerEvent.TARGET_AREA_CAPTURE_GROUP_UPDATED);
        attach(pnlInjectorRuleEditor.jchkRuleEnabled,RulesControllerEvent.ENABLED_UPDATED);
        attach(pnlInjectorRuleEditor.jRadioTargetMethodAuto,RulesControllerEvent.TARGET_METHOD_AUTO_SET);
        attach(pnlInjectorRuleEditor.jRadioTargetMethodCustomAuto,RulesControllerEvent.TARGET_METHOD_CUSTOM_AUTO_SET);
        attach(pnlInjectorRuleEditor.jRadioTargetMethodRegex,RulesControllerEvent.TARGET_METHOD_REGEX_SET);
        attach(pnlInjectorRuleEditor.jtxtCustomAutoRegex,RulesControllerEvent.CUSTOM_AUTO_REGEX_UPDATED);
        attach(pnlInjectorRuleEditor.jspnCustomAutoNameCaptureGroup,RulesControllerEvent.CUSTOM_AUTO_NAME_CAPTURE_GROUP_UPDATED);
        attach(pnlInjectorRuleEditor.jspnCustomAutoValueCaptureGroup,RulesControllerEvent.CUSTOM_AUTO_VALUE_CAPTURE_GROUP_UPDATED);
        attach(pnlInjectorRuleEditor.jchkAutoName,RulesControllerEvent.CUSTOM_AUTO_AUTONAME_UPDATED);
        checkRegex(pnlInjectorRuleEditor.jtxtRuleScope);
        checkRegex(pnlInjectorRuleEditor.jtxtTargetAreaRegex);
        checkRegex(pnlInjectorRuleEditor.jtxtCustomAutoRegex);
        attachSelection(pnlInjectorRules.jtblInjectorRules, RulesControllerEvent.RULES_ROW_SELECTION_UPDATE);
        attachTableModelChangeListener(pnlInjectorRules.jtblInjectorRules.getModel(),RulesControllerEvent.RULES_TABLE_MODEL_UPDATE);
        attach(jfrmTestRunnerOutput.btnCancel,RulesControllerEvent.CANCEL_TEST);


    }

    @Override
    protected void handleEvent(RulesModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case RULE_STACK_CHANGED:
                break;
            case CUSTOM_AUTO_TARGET_NAME_AUTOGENERATE_CHANGED:
                break;
            case CUSTOM_AUTO_TARGET_VALUE_CAPTURE_GROUP_CHANGED:
                pnlInjectorRuleEditor.jspnCustomAutoValueCaptureGroup.setValue((Integer)next);
                break;
            case CUSTOM_AUTO_TARGET_NAME_CAPTURE_GROUP_CHANGED:
                pnlInjectorRuleEditor.jspnCustomAutoNameCaptureGroup.setValue((Integer)next);
                break;
            case CUSTOM_AUTO_TARGET_NAME_REGEX_CHANGED:
                break;
            case CUSTOM_AUTO_TARGET_VALUE_REGEX_CHANGED:
                break;
            case RULE_TARGETING_METHOD_CHANGED:
                switch ((TargetingMethod)next) {
                    case AUTO:
                        pnlInjectorRuleEditor.targetMethodRadioGroup.setSelected(pnlInjectorRuleEditor.jRadioTargetMethodAuto.getModel(),true);
                        break;
                    case CUSTOM_AUTO:
                        pnlInjectorRuleEditor.targetMethodRadioGroup.setSelected(pnlInjectorRuleEditor.jRadioTargetMethodCustomAuto.getModel(),true);
                        break;
                    case REGEX:
                        pnlInjectorRuleEditor.targetMethodRadioGroup.setSelected(pnlInjectorRuleEditor.jRadioTargetMethodRegex.getModel(),true);
                        break;
                }
                pnlInjectorRuleEditor.toggleInputStatusByTargetMethod((TargetingMethod)next);
                break;
            case RULE_ENABLED_CHANGED:
                break;
            case TARGET_AREA_CAPTURE_GROUP_CHANGED:
                pnlInjectorRuleEditor.jspnTargetAreaCaptureGroup.setValue((Integer)next);
                break;
            case TARGET_AREA_REGEX_CHANGED:
                break;
            case RULE_DESCRIPTION_CHANGED:
                break;
            case RULE_SCOPE_CHANGED:
                break;
            case RULE_NAME_CHANGED:
                break;
            case RULES_LOADED:
                if ( pnlInjectorRules.jtblInjectorRules.getRowCount() > 0 ) {
                    pnlInjectorRules.jtblInjectorRules.setRowSelectionInterval(0,0);
                }
                break;
            case RULE_ID_CHANGED:
                UIUtil.selectTableRowById(pnlInjectorRules.jtblInjectorRules,(String)next);
                // General
                pnlInjectorRuleEditor.jtxtRuleName.setText(getModel().getRuleName());
                pnlInjectorRuleEditor.jtxtRuleScope.setText(getModel().getRuleScopeRegex());
                pnlInjectorRuleEditor.jtxtRuleDescription.setText(getModel().getRuleDescription());
                pnlInjectorRuleEditor.jchkRuleEnabled.setSelected(getModel().isRuleEnabled());
                pnlInjectorRuleEditor.jtxtTargetAreaRegex.setText(getModel().getTargetAreaRegex());
                pnlInjectorRuleEditor.jspnTargetAreaCaptureGroup.setValue(getModel().getTargetAreaValueCaptureGroup());

                // Rule config
                pnlInjectorRuleEditor.jtxtCustomAutoRegex.setText(getModel().getCustomAutoTargetRegex());
                pnlInjectorRuleEditor.jchkAutoName.setSelected(getModel().isCustomAutoTargetAutoGenerateName());
                pnlInjectorRuleEditor.jspnCustomAutoNameCaptureGroup.setValue(getModel().getCustomAutoTargetNameCaptureGroup());
                pnlInjectorRuleEditor.jspnCustomAutoValueCaptureGroup.setValue(getModel().getCustomAutoTargetValueCaptureGroup());
                pnlInjectorRules.jbtnTest.setEnabled(getModel().isRuleEnabled() == true ? true : false );
                break;
            case LAST_RULE_ID_CHANGED:
                break;
            case RULE_EDITOR_STATE_CHANGED:
                pnlInjectorRules.updateEditorButtonState((EditorState) next);
                pnlInjectorRuleEditor.updateEditorInputState((EditorState) next);
                break;
            case RULE_SAVED:
                getModel().updateRulesTableModel((String)next);
                UIUtil.selectTableRowById(pnlInjectorRules.jtblInjectorRules,(String)next);
                break;
            case RULE_DELETED:
                int prevRowNum = UIUtil.getRowNumberById(getModel().getInjectorRulesTableModel(),(String)next);
                getModel().removeFromTableModel((String)next);
                UIUtil.selectNextAvailableRow(pnlInjectorRules.jtblInjectorRules,prevRowNum);
                if ( getModel().getInjectorRulesTableModel().getRowCount() == 0 ) {
                    getModel().resetRule();
                    getModel().setRuleEditorState(EditorState.INITIAL);
                    pnlInjectorRuleEditor.setEnabled(false);
                }
                break;
            case LAST_ERROR_ALERT_CHANGED:
                if ( getModel().getLastErrorAlert() != null ) {
                    JOptionPane.showMessageDialog(parentComponent, getModel().getLastErrorAlert().getAlertMessage(), getModel().getLastErrorAlert().getAlertTitle(), JOptionPane.ERROR_MESSAGE);
                    getModel().setLastErrorAlert(null);
                }
                break;

            case TEST_STATE_CHANGED:
                if ((boolean)next) {
                    pnlInjectorRules.jbtnTest.setEnabled(false);
                    jfrmTestRunnerOutput.setTitle(String.format("Testing rule %s", getModel().getRuleName()));
                    jfrmTestRunnerOutput.lblProgressMessage.setText("Preparing insertion points");
                    jfrmTestRunnerOutput.setVisible(true);
                }
                else {
                    pnlInjectorRules.jbtnTest.setEnabled(true);
                    jfrmTestRunnerOutput.setVisible(false);
                }
                break;
            case TEST_TOTAL_TASKS_CHANGED:
                jfrmTestRunnerOutput.jProgressBarTestStatus.setMaximum((Integer)next);
                updateTestRunnerOutputStatusMsg();
                break;
            case TEST_CURRENT_TASK_CHANGED:
                updateTestRunnerOutputStatusMsg();
                jfrmTestRunnerOutput.jProgressBarTestStatus.setValue((Integer)next);
                break;
            default:
                Logger.log("ERROR", String.format("Unknown event %s received by %s", event.name(), this.getClass().getSimpleName()));
        }
    }

    private void updateTestRunnerOutputStatusMsg() {
        jfrmTestRunnerOutput.lblProgressMessage.setText(String.format("Sending request %d of %d", getModel().getTestCurrentTask(),getModel().getTestTotalTasks()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(RulesModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
}
