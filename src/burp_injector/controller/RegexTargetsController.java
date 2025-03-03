package burp_injector.controller;

import burp_injector.enums.EditorState;
import burp_injector.event.controller.RegexTargetsControllerEvent;
import burp_injector.model.RegexTargetsModel;
import burp_injector.mvc.AbstractController;
import burp_injector.util.UIUtil;
import java.beans.PropertyChangeEvent;

/**
 * Rules targets controller
 */
public class RegexTargetsController  extends AbstractController<RegexTargetsControllerEvent, RegexTargetsModel>  {


    public RegexTargetsController(RegexTargetsModel model) {
        super(model);
    }

    @Override
    protected void handleEvent(RegexTargetsControllerEvent event, Object previous, Object next) {
        switch ( event ) {
            case TARGETS_TABLE_MODEL_UPDATED:
                break;
            case CANCEL:
                getModel().resetTarget();
                getModel().setTargetEditorState(EditorState.INITIAL);
                break;
            case DELETE:
                getModel().deleteTarget();
                break;
            case SAVE:
                getModel().saveTarget();
                break;
            case NEW:
                getModel().newTarget();
                getModel().setTargetEditorState(EditorState.CREATE);
                break;
            case REGEX_NAME_UPDATED:
                getModel().setTargetName((String)next);
                break;
            case REGEX_PATTERN_UPDATED:
                getModel().setTargetRegex((String)next);
                break;
            case REGEX_CAPTURE_GROUP_UPDATED:
                getModel().setTargetCaptureGroup((Integer) next);
                break;
            case TARGETS_ROW_SELECTION_UPDATED:
                if ( (Integer) next >= 0 ) {
                    getModel().loadTarget(UIUtil.getIdByRowNumber(getModel().getTargetsTableModel(), (Integer) next));
                }
                break;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(RegexTargetsControllerEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }
}
