package burp_injector.model;

import burp_injector.config.AbstractConfig;
import burp_injector.enums.EditorState;
import burp_injector.event.model.RegexTargetsModelEvent;
import burp_injector.model.data.InjectorTarget;
import burp_injector.mvc.AbstractModel;
import burp_injector.util.RegexUtil;
import burp_injector.util.UIUtil;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Regex target model
 */
public class RegexTargetsModel extends AbstractModel<RegexTargetsModelEvent> {
    private String lastTargetId = null;
    private String targetId = null;
    private String targetName = null;
    private String targetRegex = null;
    private int targetCaptureGroup = 1;
    private EditorState targetEditorState = EditorState.DISABLED;
    private ArrayList<InjectorTarget> targets = new ArrayList<InjectorTarget>();
    private final DefaultTableModel targetsTableModel = new DefaultTableModel();
    private String lastSaveError = null;

    public RegexTargetsModel() {
        super();
        for (String col : new String[] {
                "ID",
                "Name",
                "Regex"}
        ) {
            this.targetsTableModel.addColumn(col);
        }
    }

    /*
        Config
     */
    @Override
    public void load(AbstractConfig config) {

    }

    @Override
    public void save(AbstractConfig config) {

    }

    /*
        Getters / Setters
     */

    public String getLastTargetId() {
        return lastTargetId;
    }

    public void setLastTargetId(String lastTargetId) {
        var old = this.lastTargetId;
        this.lastTargetId = lastTargetId;
        emit(RegexTargetsModelEvent.LAST_TARGET_ID_CHANGED, old, lastTargetId);
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        var old = this.targetId;
        this.targetId = targetId;
        emit(RegexTargetsModelEvent.TARGET_ID_CHANGED, old, targetId);
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        var old = this.targetName;
        this.targetName = targetName;
        emit(RegexTargetsModelEvent.TARGET_NAME_CHANGED, old, targetName);
    }

    public String getTargetRegex() {
        return targetRegex;
    }

    public void setTargetRegex(String targetRegex) {
        var old = this.targetRegex;
        this.targetRegex = targetRegex;
        emit(RegexTargetsModelEvent.TARGET_REGEX_CHANGED, old, targetRegex);
    }

    public int getTargetCaptureGroup() {
        return targetCaptureGroup;
    }

    public void setTargetCaptureGroup(int targetCaptureGroup) {
        var old = this.targetCaptureGroup;
        this.targetCaptureGroup = targetCaptureGroup;
        emit(RegexTargetsModelEvent.TARGET_CAPTURE_GROUP_CHANGED, old, targetCaptureGroup);
    }

    public EditorState getTargetEditorState() {
        return targetEditorState;
    }

    public void setTargetEditorState(EditorState targetEditorState) {
        var old = this.targetEditorState;
        this.targetEditorState = targetEditorState;
        emit(RegexTargetsModelEvent.TARGET_EDITOR_STATE_CHANGED, old, targetEditorState);
    }

    public ArrayList<InjectorTarget> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<InjectorTarget> targets) {
        var old = this.targets;
        this.targets = targets;
        emit(RegexTargetsModelEvent.TARGET_STACK_CHANGED, old, targets);
    }

    public DefaultTableModel getTargetsTableModel() {
        return targetsTableModel;
    }

    public String getLastSaveError() {
        return lastSaveError;
    }

    public void setLastSaveError(String lastSaveError) {
        var old = this.lastSaveError;
        this.lastSaveError = lastSaveError;
        emit(RegexTargetsModelEvent.LAST_SAVE_ERROR_CHANGED, old, lastSaveError);
    }

    /*
            Create / Update / Delete
    */

    public InjectorTarget getTargetById(String id ) {
        for ( InjectorTarget target : targets ) {
            if ( target.getId().equals(id)) {
                return target;

            }
        }
        return null;
    }

    public void newTarget() {
        resetTarget();
    }

    private boolean validateTarget() {
        if ( !RegexUtil.validateRegex(getTargetRegex())) {
            setLastSaveError("Invalid target regex");
            return false;
        }
        if ( getTargetCaptureGroup() > RegexUtil.getMatchGroupCount(getTargetRegex())) {
            setLastSaveError("Target capture group out of bounds");
            return false;
        }
        return true;
    }

    public void saveTarget() {
        if (validateTarget()) {
            InjectorTarget target = getTargetById(getTargetId());
            if ( target == null ) {
                target = new InjectorTarget(
                        getTargetId(),
                        getTargetName(),
                        getTargetRegex(),
                        getTargetCaptureGroup());
                targets.add(target);
            }
            else {
                for ( InjectorTarget currentTarget : targets ) {
                    if ( currentTarget.getId().equals(getTargetId())) {
                        target = currentTarget;
                        target.setTargetName(getTargetName());
                        target.setTargetRegex(getTargetRegex());
                        target.setTargetValueCaptureGroup(getTargetCaptureGroup());
                        break;
                    }
                }
            }
            emit(RegexTargetsModelEvent.TARGET_SAVED, null, target.getId());
        }
    }

    public void deleteTarget() {
        for ( int i = 0 ; i < targets.size(); i++ ) {
            if ( targets.get(i).getId().equals(getTargetId())) {
                emit(RegexTargetsModelEvent.TARGET_DELETED, null, targets.get(i).getId());
                targets.remove(i);
                break;
            }
        }
    }

    public void resetTarget() {
        InjectorTarget target = new InjectorTarget();
        target.setId(null);
        loadTargetObject(target);
    }

    public void loadTarget( String id ) {
        for ( InjectorTarget target : targets ) {
            if ( target.getId().equals(id)) {
                loadTargetObject(target);
                break;
            }
        }
    }

    public void loadTargetObject( InjectorTarget target ) {
        setTargetName(target.getTargetName());
        setTargetRegex(target.getTargetRegex());
        setTargetCaptureGroup(target.getTargetValueCaptureGroup());
        setTargetId(target.getId());
        setTargetEditorState(EditorState.EDIT);
    }

    public void updateTargetsTableModel(String id) {
        InjectorTarget target = getTargetById(id);
        int rowId = UIUtil.getRowNumberById(targetsTableModel, id);
        if (target != null) {
            if (rowId >= 0) {
                targetsTableModel.setValueAt(target.getTargetName(), rowId, 1);
                targetsTableModel.setValueAt(target.getTargetRegex(), rowId, 2);
            }
            else {
                targetsTableModel.insertRow(0, new Object[]{
                        target.getId(),
                        target.getTargetName(),
                        target.getTargetRegex()
                });
            }
        }
    }

    public void removeFromTableModel(String id) {
        int rowId = UIUtil.getRowNumberById(targetsTableModel,id);
        if ( rowId >= 0 ) {
            targetsTableModel.removeRow(rowId);
        }
    }
}
