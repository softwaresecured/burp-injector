package burp_injector.view;

import burp_injector.enums.EditorState;
import burp_injector.enums.TargetingMethod;
import burp_injector.event.controller.RegexTargetsControllerEvent;
import burp_injector.event.model.RegexTargetsModelEvent;
import burp_injector.event.model.RulesModelEvent;
import burp_injector.model.RegexTargetsModel;
import burp_injector.model.RulesModel;
import burp_injector.model.data.InjectorTarget;
import burp_injector.mvc.AbstractView;
import burp_injector.ui.PnlRegexTargetEditor;
import burp_injector.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Regex targets view
 */
public class RegexTargetsView extends AbstractView<RegexTargetsControllerEvent, RegexTargetsModel, RegexTargetsModelEvent> {
    public PnlRegexTargetEditor pnlRegexTargetEditor;
    private Component parentComponent;
    public RegexTargetsView(RegexTargetsModel model) {
        super(model);
        pnlRegexTargetEditor = new PnlRegexTargetEditor(model);
        pnlRegexTargetEditor.setEnabled(false);
    }

    @Override
    public void attachListeners() {

        attach(pnlRegexTargetEditor.jbtnNew,RegexTargetsControllerEvent.NEW);
        attach(pnlRegexTargetEditor.jbtnSave,RegexTargetsControllerEvent.SAVE);
        attach(pnlRegexTargetEditor.jbtnDelete,RegexTargetsControllerEvent.DELETE);
        attach(pnlRegexTargetEditor.jbtnCancel,RegexTargetsControllerEvent.CANCEL);

        attach(pnlRegexTargetEditor.jtxtRegexName,RegexTargetsControllerEvent.REGEX_NAME_UPDATED);
        attach(pnlRegexTargetEditor.jtxtRegexPattern,RegexTargetsControllerEvent.REGEX_PATTERN_UPDATED);
        attach(pnlRegexTargetEditor.jspnRegexPatternCaptureGroup,RegexTargetsControllerEvent.REGEX_CAPTURE_GROUP_UPDATED);
        checkRegex(pnlRegexTargetEditor.jtxtRegexPattern);

        attachSelection(pnlRegexTargetEditor.jtblTargets, RegexTargetsControllerEvent.TARGETS_ROW_SELECTION_UPDATED);
        attachTableModelChangeListener(pnlRegexTargetEditor.jtblTargets.getModel(),RegexTargetsControllerEvent.TARGETS_TABLE_MODEL_UPDATED);
    }

    @Override
    protected void handleEvent(RegexTargetsModelEvent event, Object previous, Object next) {
        switch (event) {
            case TARGET_ID_CHANGED:
                UIUtil.selectTableRowById(pnlRegexTargetEditor.jtblTargets,(String)next);
                pnlRegexTargetEditor.jtxtRegexName.setText(getModel().getTargetName());
                pnlRegexTargetEditor.jtxtRegexPattern.setText(getModel().getTargetRegex());
                pnlRegexTargetEditor.jspnRegexPatternCaptureGroup.setValue(getModel().getTargetCaptureGroup());
                break;
            case TARGET_EDITOR_STATE_CHANGED:
                pnlRegexTargetEditor.updateEditorButtonState((EditorState) next);
                pnlRegexTargetEditor.updateEditorInputState((EditorState) next);
                break;
            case TARGET_CAPTURE_GROUP_CHANGED:
                pnlRegexTargetEditor.jspnRegexPatternCaptureGroup.setValue((Integer)next);
                break;
            case TARGET_REGEX_CHANGED:
                break;
            case TARGET_NAME_CHANGED:
                break;
            case LAST_TARGET_ID_CHANGED:
                break;
            case TARGET_STACK_CHANGED:
                getModel().resetTarget();
                getModel().getTargetsTableModel().setRowCount(0);
                for (InjectorTarget injectorTarget : getModel().getTargets()) {
                    getModel().getTargetsTableModel().insertRow(0, new Object[]{
                            injectorTarget.getId(),
                            injectorTarget.getTargetName(),
                            injectorTarget.getTargetRegex()
                    });
                }
                if ( pnlRegexTargetEditor.jtblTargets.getRowCount() > 0 ) {
                    pnlRegexTargetEditor.jtblTargets.setRowSelectionInterval(0,0);
                }
                break;
            case TARGET_SAVED:
                getModel().updateTargetsTableModel((String)next);
                getModel().loadTarget((String)next);
                break;
            case TARGET_DELETED:
                int prevRowNum = UIUtil.getRowNumberById(getModel().getTargetsTableModel(),(String)next);
                getModel().removeFromTableModel((String)next);
                UIUtil.selectNextAvailableRow(pnlRegexTargetEditor.jtblTargets,prevRowNum);
                if ( getModel().getTargetsTableModel().getRowCount() == 0 ) {
                    getModel().resetTarget();
                    getModel().setTargetEditorState(EditorState.INITIAL);
                }
                break;
            case LAST_SAVE_ERROR_CHANGED:
                if ( getModel().getLastSaveError() != null ) {
                    JOptionPane.showMessageDialog(parentComponent, (String) next, "Cannot save target", JOptionPane.ERROR_MESSAGE);
                    getModel().setLastSaveError(null);
                }
                break;
        }
    }

    protected void handleEvent(RulesModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case RULE_TARGETING_METHOD_CHANGED:
                TargetingMethod targetingMethod = (TargetingMethod) next;
                if ( targetingMethod.equals(TargetingMethod.REGEX)) {
                    pnlRegexTargetEditor.setEnabled(true);
                    getModel().setTargetEditorState(EditorState.INITIAL);
                    if ( getModel().getTargets().size() > 0 ) {
                        pnlRegexTargetEditor.jtblTargets.setRowSelectionInterval(0,0);
                    }
                }
                else {
                    pnlRegexTargetEditor.setEnabled(false);
                    getModel().resetTarget();
                }
                break;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() instanceof RegexTargetsModel) {
            handleEvent(RegexTargetsModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
        if ( evt.getSource() instanceof RulesModel) {
            handleEvent(RulesModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
}
