package burp_injector.model;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp_injector.config.AbstractConfig;
import burp_injector.config.InjectorRuleConfigExport;
import burp_injector.config.InjectorRuleExport;
import burp_injector.config.InjectorTargetExport;
import burp_injector.enums.ConfigKey;
import burp_injector.enums.EditorState;
import burp_injector.enums.ScriptType;
import burp_injector.enums.TargetingMethod;
import burp_injector.event.model.RulesModelEvent;
import burp_injector.insertionpoint.InjectorInsertionPoint;
import burp_injector.insertionpoint.InsertionPointException;
import burp_injector.model.data.ErrorAlert;
import burp_injector.model.data.InjectorRule;
import burp_injector.model.data.InjectorTarget;
import burp_injector.model.data.TestRequest;
import burp_injector.mvc.AbstractModel;
import burp_injector.threads.RequestRunner;
import burp_injector.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Rules model
 */
public class RulesModel extends AbstractModel<RulesModelEvent> {
    private MontoyaApi api;
    private RegexTargetsModel regexTargetsModel;
    private TestRequestModel testRequestModel;
    private ScriptModel scriptModel;

    private String lastRuleId = null;

    // Top level rule properties
    private String ruleId = null;
    private String ruleName = null;
    private String ruleScopeRegex = null;
    private String ruleDescription = null;
    private String targetAreaRegex = null;
    private int targetAreaValueCaptureGroup = 1;
    private boolean ruleEnabled = false;

    // Rule details
    private TargetingMethod ruleTargetingMethod = null;
    private String customAutoTargetRegex = null;
    private int customAutoTargetNameCaptureGroup = 1;
    private int customAutoTargetValueCaptureGroup = 1;
    private boolean customAutoTargetAutoGenerateName = false;

    private final DefaultTableModel injectorRulesTableModel = new DefaultTableModel();
    private EditorState ruleEditorState = EditorState.DISABLED;
    private ArrayList<InjectorRule> injectorRules = new ArrayList<InjectorRule>();

    // Rule test
    private boolean testRunning = false;
    private int testTotalTasks = 0;
    private int testCurrentTask = 0;
    private Thread testRunnerThread = null;
    private TestRunner testRunnerWorker = null;

    // Errors
    private ErrorAlert lastErrorAlert = null;


    public RulesModel() {
        super();
        for (String col : new String[] {
                "ID",
                "Enabled",
                "Name",
                "Description"}
        ) {
            this.injectorRulesTableModel.addColumn(col);
        }
    }

    /*
        Config
     */
    @Override
    public void load(AbstractConfig config) {
        String importRuleJSONContent = config.getString(ConfigKey.INJECTOR_RULES);
        if ( importRuleJSONContent != null && importRuleJSONContent.length() > 0) {
            try {
                InjectorRuleConfigExport importContent = importVariablesFromJSON(importRuleJSONContent);
                for ( InjectorRuleExport injectorRule : importContent.rules ) {
                    importRule(injectorRule);
                }
                emit(RulesModelEvent.RULES_LOADED, null, null);
            } catch (JsonProcessingException e) {
                Logger.log("ERROR","Failed to load previously saved config");
            } catch ( Exception e ) {
                nukeConfig(config);
            }
        }
    }

    public void nukeConfig(AbstractConfig config) {
        config.setString(ConfigKey.INJECTOR_RULES, null);
        config.setString(ConfigKey.TEST_PAYLOAD, null);
        config.setString(ConfigKey.TEST_HTTP_REQUEST_MAP, null);
        config.setString(ConfigKey.TEST_HTTP_URL, null);
        Logger.log("ERROR","Config is trash, nuking it. This is a bug, please report. Sorry.");
    }

    @Override
    public void save(AbstractConfig config) {
        try {
            config.setString(ConfigKey.INJECTOR_RULES, exportVariablesAsJSON(exportVariables()));
        } catch (JsonProcessingException e) {
            Logger.log("ERROR", String.format("Error exporting data: %s", e.getMessage()));
        }
    }

    public String exportVariablesAsJSON(InjectorRuleConfigExport export) throws JsonProcessingException {
        InjectorRuleConfigExport exportDataObject = exportVariables();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(exportDataObject);
    }

    public InjectorRuleConfigExport importVariablesFromJSON( String jsonStr ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        InjectorRuleConfigExport importDataObject = mapper.readValue(new String(jsonStr), InjectorRuleConfigExport.class);
        return importDataObject;
    }

    /*
        Getters / Setters
     */

    public void setApi(MontoyaApi api) {
        this.api = api;
    }

    public MontoyaApi getApi() {
        return api;
    }

    public String getLastRuleId() {
        return lastRuleId;
    }

    public void setLastRuleId(String lastRuleId) {
        var old = this.lastRuleId;
        this.lastRuleId = lastRuleId;
        emit(RulesModelEvent.LAST_RULE_ID_CHANGED, old, lastRuleId);
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        var old = this.ruleId;
        this.ruleId = ruleId;
        emit(RulesModelEvent.RULE_ID_CHANGED, old, ruleId);
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        var old = this.ruleName;
        this.ruleName = ruleName;
        emit(RulesModelEvent.RULE_NAME_CHANGED, old, ruleName);
    }

    public String getRuleScopeRegex() {
        return ruleScopeRegex;
    }

    public void setRuleScopeRegex(String ruleScopeRegex) {
        var old = this.ruleScopeRegex;
        this.ruleScopeRegex = ruleScopeRegex;
        emit(RulesModelEvent.RULE_SCOPE_CHANGED, old, ruleScopeRegex);
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        var old = this.ruleDescription;
        this.ruleDescription = ruleDescription;
        emit(RulesModelEvent.RULE_DESCRIPTION_CHANGED, old, ruleDescription);
    }

    public String getTargetAreaRegex() {
        return targetAreaRegex;
    }

    public void setTargetAreaRegex(String targetAreaRegex) {
        var old = this.targetAreaRegex;
        this.targetAreaRegex = targetAreaRegex;
        emit(RulesModelEvent.TARGET_AREA_REGEX_CHANGED, old, targetAreaRegex);
    }

    public int getTargetAreaValueCaptureGroup() {
        return targetAreaValueCaptureGroup;
    }

    public void setTargetAreaValueCaptureGroup(int targetAreaValueCaptureGroup) {
        var old = this.targetAreaValueCaptureGroup;
        this.targetAreaValueCaptureGroup = targetAreaValueCaptureGroup;
        emit(RulesModelEvent.TARGET_AREA_CAPTURE_GROUP_CHANGED, old, targetAreaValueCaptureGroup);
    }

    public boolean isRuleEnabled() {
        return ruleEnabled;
    }

    public void setRuleEnabled(boolean ruleEnabled) {
        var old = this.ruleEnabled;
        this.ruleEnabled = ruleEnabled;
        emit(RulesModelEvent.RULE_ENABLED_CHANGED, old, ruleEnabled);
    }

    public TargetingMethod getRuleTargetingMethod() {
        return ruleTargetingMethod;
    }

    public void setRuleTargetingMethod(TargetingMethod ruleTargetingMethod) {
        var old = this.ruleTargetingMethod;
        this.ruleTargetingMethod = ruleTargetingMethod;
        emit(RulesModelEvent.RULE_TARGETING_METHOD_CHANGED, old, ruleTargetingMethod);
    }


    public String getCustomAutoTargetRegex() {
        return customAutoTargetRegex;
    }

    public void setCustomAutoTargetRegex(String customAutoTargetNameRegex) {
        var old = this.customAutoTargetRegex;
        this.customAutoTargetRegex = customAutoTargetNameRegex;
        emit(RulesModelEvent.CUSTOM_AUTO_TARGET_NAME_REGEX_CHANGED, old, customAutoTargetNameRegex);
    }

    public int getCustomAutoTargetNameCaptureGroup() {
        return customAutoTargetNameCaptureGroup;
    }

    public void setCustomAutoTargetNameCaptureGroup(int customAutoTargetNameCaptureGroup) {
        var old = this.customAutoTargetNameCaptureGroup;
        this.customAutoTargetNameCaptureGroup = customAutoTargetNameCaptureGroup;
        emit(RulesModelEvent.CUSTOM_AUTO_TARGET_NAME_CAPTURE_GROUP_CHANGED, old, customAutoTargetNameCaptureGroup);
    }

    public int getCustomAutoTargetValueCaptureGroup() {
        return customAutoTargetValueCaptureGroup;
    }

    public void setCustomAutoTargetValueCaptureGroup(int customAutoTargetValueCaptureGroup) {
        var old = this.customAutoTargetValueCaptureGroup;
        this.customAutoTargetValueCaptureGroup = customAutoTargetValueCaptureGroup;
        emit(RulesModelEvent.CUSTOM_AUTO_TARGET_VALUE_CAPTURE_GROUP_CHANGED, old, customAutoTargetValueCaptureGroup);
    }

    public boolean isCustomAutoTargetAutoGenerateName() {
        return customAutoTargetAutoGenerateName;
    }

    public void setCustomAutoTargetAutoGenerateName(boolean customAutoTargetAutoGenerateName) {
        var old = this.customAutoTargetAutoGenerateName;
        this.customAutoTargetAutoGenerateName = customAutoTargetAutoGenerateName;
        emit(RulesModelEvent.CUSTOM_AUTO_TARGET_NAME_AUTOGENERATE_CHANGED, old, customAutoTargetAutoGenerateName);
    }

    public DefaultTableModel getInjectorRulesTableModel() {
        return injectorRulesTableModel;
    }

    public EditorState getRuleEditorState() {
        return ruleEditorState;
    }

    public void setRuleEditorState(EditorState ruleEditorState) {
        var old = this.ruleEditorState;
        this.ruleEditorState = ruleEditorState;
        emit(RulesModelEvent.RULE_EDITOR_STATE_CHANGED, old, ruleEditorState);

    }

    public ArrayList<InjectorRule> getInjectorRules() {
        return injectorRules;
    }

    public void setInjectorRules(ArrayList<InjectorRule> injectorRules) {
        var old = this.injectorRules;
        this.injectorRules = injectorRules;
        emit(RulesModelEvent.RULE_STACK_CHANGED, old, injectorRules);
    }

    public RegexTargetsModel getRegexTargetsModel() {
        return regexTargetsModel;
    }

    public void setRegexTargetsModel(RegexTargetsModel regexTargetsModel) {
        this.regexTargetsModel = regexTargetsModel;
    }

    public ErrorAlert getLastErrorAlert() {
        return lastErrorAlert;
    }

    public void setLastErrorAlert(ErrorAlert lastErrorAlert) {
        var old = this.lastErrorAlert;
        this.lastErrorAlert = lastErrorAlert;
        emit(RulesModelEvent.LAST_ERROR_ALERT_CHANGED, old, lastErrorAlert);
    }

    public TestRequestModel getTestRequestModel() {
        return testRequestModel;
    }

    public boolean isTestRunning() {
        return testRunning;
    }

    public void setTestRunning(boolean testRunning) {
        var old = this.testRunning;
        this.testRunning = testRunning;
        emit(RulesModelEvent.TEST_STATE_CHANGED, old, testRunning);
    }

    public int getTestTotalTasks() {
        return testTotalTasks;
    }

    public void setTestTotalTasks(int testTotalTasks) {
        var old = this.testTotalTasks;
        this.testTotalTasks = testTotalTasks;
        emit(RulesModelEvent.TEST_TOTAL_TASKS_CHANGED, old, testTotalTasks);
    }

    public int getTestCurrentTask() {
        return testCurrentTask;
    }

    public void setTestCurrentTask(int testCurrentTask) {
        var old = this.testCurrentTask;
        this.testCurrentTask = testCurrentTask;
        emit(RulesModelEvent.TEST_CURRENT_TASK_CHANGED, old, testCurrentTask);
    }

    public void setTestRequestModel(TestRequestModel testRequestModel) {
        this.testRequestModel = testRequestModel;
    }

    public ScriptModel getScriptModel() {
        return scriptModel;
    }

    public void setScriptModel(ScriptModel scriptModel) {
        this.scriptModel = scriptModel;
    }

    public String getTargetArea() {
        if ( testRequestModel.getTestHttpRequestStr() != null ) {
            if ( RegexUtil.validateRegex(getTargetAreaRegex())) {
                if ( getTargetAreaValueCaptureGroup() <= RegexUtil.getMatchGroupCount(getTargetAreaRegex())) {
                    Pattern p = Pattern.compile(getTargetAreaRegex(),Pattern.DOTALL|Pattern.MULTILINE);
                    Matcher m = p.matcher(testRequestModel.getTestHttpRequestStr().toString());
                    if ( m.find() ) {
                        return m.group(getTargetAreaValueCaptureGroup());
                    }
                }
            }
        }
        return null;
    }

    /*
            Create / Update / Delete
    */

    public InjectorRule getRuleById( String id ) {
        for ( InjectorRule rule : injectorRules ) {
            if ( rule.getId().equals(id)) {
                return rule;

            }
        }
        return null;
    }

    public void newRule() {
        resetRule();
    }

    public InjectorRule getRuleByName( String name ) {
        for ( InjectorRule rule : injectorRules ) {
            if ( rule.getName().equals(name)) {
                return rule;
            }
        }
        return null;
    }

    private boolean validateRule() {

        if ( getRuleName() == null ) {
            setLastErrorAlert(new ErrorAlert("Cannot save rule",String.format("A rule must have a name")));
            return false;
        }

        if ( getRuleByName(getRuleName()) != null ) {
            if ( getRuleByName(getRuleName()).getId() != getRuleId() ) {
                setLastErrorAlert(new ErrorAlert("Cannot save rule",String.format("A rule with the name \"%s\" already exists", getRuleName())));
                return false;
            }
        }

        if ( getRuleScopeRegex() == null ) {
            setLastErrorAlert(new ErrorAlert("Cannot save rule",String.format("A rule must have a rule scope")));
            return false;
        }

        if ( getTargetAreaRegex() == null ) {
            setLastErrorAlert(new ErrorAlert("Cannot save rule",String.format("A rule must a target area regex")));
            return false;
        }

        if ( getRuleScopeRegex() != null ) {
            if (RegexUtil.getMatchGroupCount(getRuleScopeRegex()) > 0 ) {
                setLastErrorAlert(new ErrorAlert("Cannot save rule",String.format("A rule scope regex must not have capture groups")));
                return false;
            }
        }

        if ( !RegexUtil.validateRegex(getRuleScopeRegex())) {
            setLastErrorAlert(new ErrorAlert("Cannot save rule","Invalid rule scope regex"));
            return false;
        }
        if ( !RegexUtil.validateRegex(getTargetAreaRegex())) {
            setLastErrorAlert(new ErrorAlert("Cannot save rule","Invalid target regex"));
            return false;
        }
        if ( getTargetAreaValueCaptureGroup() > RegexUtil.getMatchGroupCount(getTargetAreaRegex())) {
            setLastErrorAlert(new ErrorAlert("Cannot save rule","Target regex capture group out of bounds"));
            return false;
        }

        if ( getRuleTargetingMethod().equals(TargetingMethod.CUSTOM_AUTO)) {
            if ( !RegexUtil.validateRegex(getCustomAutoTargetRegex())) {
                setLastErrorAlert(new ErrorAlert("Cannot save rule","Invalid auto target name regex"));
                return false;
            }
            if ( !RegexUtil.validateRegex(getCustomAutoTargetRegex())) {
                setLastErrorAlert(new ErrorAlert("Cannot save rule","Invalid auto target value regex"));
                return false;
            }
            if ( getCustomAutoTargetNameCaptureGroup() > RegexUtil.getMatchGroupCount(getCustomAutoTargetRegex())) {
                setLastErrorAlert(new ErrorAlert("Cannot save rule","Target name capture group out of bounds"));
                return false;
            }
            if ( getCustomAutoTargetValueCaptureGroup() > RegexUtil.getMatchGroupCount(getCustomAutoTargetRegex())) {
                setLastErrorAlert(new ErrorAlert("Cannot save rule","Target value capture group out of bounds"));
                return false;
            }
        }
        return true;
    }

    private void importRule( InjectorRuleExport ruleExport ) {
        InjectorRule injectorRule = new InjectorRule(
                ruleExport.id,
                ruleExport.name,
                ruleExport.ruleScopeRegex,
                ruleExport.targetAreaRegex,
                ruleExport.targetAreaValueCaptureGroup,
                ruleExport.ruleDescription,
                ruleExport.ruleEnabled,
                ruleExport.ruleTargetMethod,
                ruleExport.autoTargetAutoGenerateName,
                ruleExport.customAutoTargetRegex,
                ruleExport.customAutoTargetNameCaptureGroup,
                ruleExport.customAutoTargetValueCaptureGroup
        );

        injectorRule.setPayloadProcessScript(ruleExport.payloadProcessScript);
        injectorRule.setEncodeScript(ruleExport.encodeScript);
        injectorRule.setDecodeScript(ruleExport.decodeScript);
        for ( InjectorTargetExport injectorTargetExport : ruleExport.targets ) {
            InjectorTarget injectorTarget = new InjectorTarget(
                    injectorTargetExport.id,
                    injectorTargetExport.targetName,
                    injectorTargetExport.targetRegex,
                    injectorTargetExport.targetValueCaptureGroup
            );
            injectorRule.getTargets().add(injectorTarget);
        }
        loadRuleObject(injectorRule);
        saveRule();
    }

    public void saveRule() {
        if ( validateRule() ) {
            InjectorRule rule = getRuleById(getRuleId());
            if ( rule == null ) {
                rule = new InjectorRule(
                        getRuleId(),
                        getRuleName(),
                        getRuleScopeRegex(),
                        getTargetAreaRegex(),
                        getTargetAreaValueCaptureGroup(),
                        getRuleDescription(),
                        isRuleEnabled(),
                        getRuleTargetingMethod(),
                        isCustomAutoTargetAutoGenerateName(),
                        getCustomAutoTargetRegex(),
                        getCustomAutoTargetNameCaptureGroup(),
                        getCustomAutoTargetValueCaptureGroup()
                );
                rule.setEncodeScript(scriptModel.getEncodeScript());
                rule.setDecodeScript(scriptModel.getDecodeScript());
                rule.setPayloadProcessScript(scriptModel.getPayloadProcessScript());
                rule.setTargets(getRegexTargetsModel().getTargets());
                injectorRules.add(rule);
            }
            else {
                for ( InjectorRule currentRule : injectorRules ) {
                    if ( currentRule.getId().equals(getRuleId())) {
                        rule = currentRule;
                        rule.setName(getRuleName());
                        rule.setRuleScopeRegex(getRuleScopeRegex());
                        rule.setTargetAreaRegex(getTargetAreaRegex());
                        rule.setTargetAreaValueCaptureGroup(getTargetAreaValueCaptureGroup());
                        rule.setRuleDescription(getRuleDescription());
                        rule.setRuleEnabled(isRuleEnabled());
                        rule.setRuleTargetMethod(getRuleTargetingMethod());
                        rule.setAutoTargetAutoGenerateName(isCustomAutoTargetAutoGenerateName());
                        rule.setCustomAutoTargetRegex(getCustomAutoTargetRegex());
                        rule.setCustomAutoTargetNameCaptureGroup(getCustomAutoTargetNameCaptureGroup());
                        rule.setCustomAutoTargetValueCaptureGroup(getCustomAutoTargetValueCaptureGroup());
                        rule.setTargets(getRegexTargetsModel().getTargets());
                        rule.setEncodeScript(scriptModel.getEncodeScript());
                        rule.setDecodeScript(scriptModel.getDecodeScript());
                        rule.setPayloadProcessScript(scriptModel.getPayloadProcessScript());
                        break;
                    }
                }
            }
            TestRequest testRequest = new TestRequest(
                    testRequestModel.getTestHttpRequestStr(),
                    testRequestModel.getBaseURL()
            );
            testRequestModel.updateTestRequest(rule.getId(),testRequest);
            emit(RulesModelEvent.RULE_SAVED, null, rule.getId());
        }
    }

    public void deleteRule() {
        for ( int i = 0 ; i < injectorRules.size(); i++ ) {
            if ( injectorRules.get(i).getId().equals(getRuleId())) {
                testRequestModel.removeTestRequest(getRuleId());
                emit(RulesModelEvent.RULE_DELETED, null, injectorRules.get(i).getId());
                injectorRules.remove(i);
                setLastRuleId(null);
                break;
            }
        }
    }

    public void resetRule() {
        InjectorRule rule = new InjectorRule();
        rule.setId(null);
        loadRuleObject(rule);
    }

    public void loadRule( String id ) {
        for ( InjectorRule rule : injectorRules ) {
            if ( rule.getId().equals(id)) {
                loadRuleObject(rule);
                break;
            }
        }
    }

    public void loadRuleObject( InjectorRule rule ) {
        setRuleName(rule.getName());
        setRuleScopeRegex(rule.getRuleScopeRegex());
        setRuleDescription(rule.getRuleDescription());
        setTargetAreaRegex(rule.getTargetAreaRegex());
        setTargetAreaValueCaptureGroup(rule.getTargetAreaValueCaptureGroup());
        setRuleEnabled(rule.isRuleEnabled());
        setRuleTargetingMethod(rule.getRuleTargetMethod());
        setCustomAutoTargetRegex(rule.getCustomAutoTargetRegex());
        setCustomAutoTargetNameCaptureGroup(rule.getCustomAutoTargetNameCaptureGroup());
        setCustomAutoTargetValueCaptureGroup(rule.getCustomAutoTargetValueCaptureGroup());
        setCustomAutoTargetAutoGenerateName(rule.isAutoTargetAutoGenerateName());
        getRegexTargetsModel().setTargets(rule.getTargets());
        setRuleId(rule.getId());
        scriptModel.cancelScript();
        scriptModel.loadScript(ScriptType.DECODE,rule.getDecodeScript());
        scriptModel.loadScript(ScriptType.ENCODE,rule.getEncodeScript());
        scriptModel.loadScript(ScriptType.PAYLOAD,rule.getPayloadProcessScript());
        scriptModel.setLastExecutionTimeMs(0);
        setRuleEditorState(EditorState.EDIT);
        scriptModel.setActiveScriptTab(ScriptType.DECODE);
        if ( rule.getId() != null ) {
            setLastRuleId(rule.getId());
        }
        if ( rule.getId() != null ) {
            testRequestModel.loadTestRequest(rule.getId());
        }
    }

    public void updateRulesTableModel(String id) {
        InjectorRule rule = getRuleById(id);
        int rowId = UIUtil.getRowNumberById(injectorRulesTableModel, id);
        if (rule != null) {
            if (rowId >= 0) {
                injectorRulesTableModel.setValueAt(rule.isRuleEnabled() ? "Enabled" : "Disabled", rowId, 1);
                injectorRulesTableModel.setValueAt(rule.getName(), rowId, 2);
                injectorRulesTableModel.setValueAt(rule.getRuleDescription(), rowId, 3);
            }
            else {
                injectorRulesTableModel.insertRow(0, new Object[]{
                        rule.getId(),
                        rule.isRuleEnabled() ? "Enabled" : "Disabled",
                        rule.getName(),
                        rule.getRuleDescription()
                });
            }
        }
    }

    public void removeFromTableModel(String id) {
        int rowId = UIUtil.getRowNumberById(injectorRulesTableModel,id);
        if ( rowId >= 0 ) {
            injectorRulesTableModel.removeRow(rowId);
        }
    }

    public void setScriptsModel(ScriptModel scriptModel) {
        this.scriptModel = scriptModel;
    }

    // Export stuff
    public InjectorRuleConfigExport exportVariables() {
        InjectorRuleConfigExport export = new InjectorRuleConfigExport();
        export.rules = new InjectorRuleExport[injectorRules.size()];
        for ( int i = 0; i < injectorRules.size(); i++ ) {
            export.rules[i] = injectorRules.get(i).toInjectorRuleExport();
        }
        return export;
    }

    public void cancelRuleTest() {
        if ( testRunnerWorker != null ) {
            testRunnerWorker.cancel();
            try {
                testRunnerThread.join();
                testRunnerWorker = null;
            } catch (InterruptedException e) {
                Logger.log("ERROR","Error joining test runner thread");
            }
        }
    }

    public void testRule(String ruleId) {
        if ( getScriptModel().getPayloadTest() != null && getScriptModel().getPayloadTest().length() > 0) {
            testRunnerWorker = new TestRunner(ruleId);
            testRunnerThread = new Thread(testRunnerWorker);
            testRunnerThread.start();
            Logger.log("INFO","Test started");
        }
        else {
            setLastErrorAlert(new ErrorAlert("Cannot test rule","You must set a test payload"));
        }
    }

    private class TestRunner implements Runnable {
        private String testRuleId = null;
        private boolean cancelled = false;
        private Thread testRequestRunnerThread = null;
        private InjectorInsertionPoint currentInsertionPoint = null;
        private InjectorRule testRule = null;
        public TestRunner(String ruleId) {
            testRuleId = ruleId;
        }

        public void cancel() {
            try {
                cancelled = true;
                if (testRule != null) {
                    testRule.cancelBuildInsertionPoints();
                }
                if (currentInsertionPoint != null) {
                    currentInsertionPoint.abort();
                }
                if (testRequestRunnerThread != null && testRequestRunnerThread.isAlive()) {
                    testRequestRunnerThread.interrupt();
                }
            } catch ( Exception e ) {
                Logger.log("ERROR", String.format("Exception while cancelling test: %s", e.getMessage()));
            }
        }
        @Override
        public void run() {
            Logger.log("INFO","Test runner thread started");
            setTestRunning(true);
            testRule = getRuleById(testRuleId);
            if ( testRule != null ) {
                if ( getTestRequestModel().getHttpRequest() != null && scriptModel.getPayloadTest() != null ) {
                    ArrayList<InjectorInsertionPoint> insertionPoints = testRule.getInsertionPointsCancellable(HttpRequest.httpRequest(getTestRequestModel().getTestHttpRequestStr()));
                    setTestTotalTasks(insertionPoints.size());
                    setTestCurrentTask(1);
                    Logger.log("INFO", String.format("Testing %d insertion points", insertionPoints.size()));
                    for ( InjectorInsertionPoint injectorInsertionPoint : insertionPoints ) {
                        currentInsertionPoint = injectorInsertionPoint;
                        if( cancelled ) {
                            break;
                        }
                        try {
                            injectorInsertionPoint.init();
                            if ( cancelled ) { break; }
                            HttpRequest testRequest = injectorInsertionPoint.buildHttpRequestWithPayload(ByteArray.byteArray(scriptModel.getPayloadTest().getBytes()));
                            if ( cancelled ) { break; }
                            if ( testRequest == null ) {
                                Logger.log("ERROR","Insertion point buildHttpRequestWithPayload operation returned null");
                                continue;
                            }
                            RequestRunner testRequestRunner = new RequestRunner(RequestUtil.rebuildWithService(testRequest,getTestRequestModel().getBaseURL()));
                            testRequestRunnerThread = new Thread(testRequestRunner);
                            testRequestRunnerThread.start();
                            while ( testRequestRunnerThread.isAlive() && ! cancelled) {
                                testRequestRunnerThread.join(100);
                            }
                            setTestCurrentTask(getTestCurrentTask()+1);
                        } catch (InsertionPointException e) {
                            Logger.log("ERROR", String.format("Error sending test request - %s", e.getMessage()));
                        } catch (InterruptedException e) {
                            Logger.log("ERROR", String.format("Error joining test thread - %s", e.getMessage()));
                        }
                    }

                }
                else {
                    Logger.log("ERROR", String.format("Test payload or test request is null"));
                }
            }
            else {
                Logger.log("ERROR", String.format("Error sending test request - Could not load rule %s", testRuleId));
            }
            setTestTotalTasks(0);
            setTestCurrentTask(0);
            setTestRunning(false);
        }
    }
}
