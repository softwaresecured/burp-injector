package burp_injector.model.data;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp_injector.targeting.AutoTargeter;
import burp_injector.insertionpoint.InjectorInsertionPoint;
import burp_injector.config.InjectorRuleExport;
import burp_injector.config.InjectorTargetExport;
import burp_injector.constants.ScriptConstants;
import burp_injector.enums.ScriptType;
import burp_injector.enums.TargetingMethod;
import burp_injector.insertionpoint.InsertionPointException;
import burp_injector.python.PythonScript;
import burp_injector.util.InjectorUtil;
import burp_injector.util.Logger;
import burp_injector.util.RegexUtil;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/**
 * The injector rule object
 */
public class InjectorRule {
    private String id = UUID.randomUUID().toString();
    private String name = null;
    private String ruleScopeRegex = null;
    private String targetAreaRegex = null;
    private int targetAreaValueCaptureGroup = 1;
    private String ruleDescription = null;
    private boolean ruleEnabled = false;
    private TargetingMethod ruleTargetMethod = TargetingMethod.AUTO;
    private boolean autoTargetAutoGenerateName = false;
    private String customAutoTargetRegex = null;
    private int customAutoTargetNameCaptureGroup = 1;
    private int customAutoTargetValueCaptureGroup = 1;

    private Pattern ruleScopeRegexPattern = null;
    private Pattern targetAreaRegexPattern = null;
    private Pattern customAutoTargetRegexPattern = null;

    // Scripts
    private String encodeScript = ScriptConstants.ENCODE_SCRIPT_USER_STUB;
    private String decodeScript = ScriptConstants.DECODER_SCRIPT_USER_STUB;
    private String payloadProcessScript = ScriptConstants.PAYLOAD_PROCESS_SCRIPT_USER_STUB;

    private ArrayList<InjectorTarget> targets = new ArrayList<InjectorTarget>();

    private Thread insertionPointBuilderThread = null;
    private InsertionPointBuilderWorker insertionPointBuilderWorker = null;

    public InjectorRule() {
    }

    public InjectorRule(String id, String name, String ruleScopeRegex, String targetAreaRegex, int targetAreaValueCaptureGroup, String ruleDescription, boolean ruleEnabled, TargetingMethod ruleTargetMethod, boolean autoTargetAutoGenerateName,String customAutoTargetRegex, int customAutoTargetNameCaptureGroup, int customAutoTargetValueCaptureGroup) {
        if ( id != null ) {
            this.id = id;
        }
        this.name = name;
        this.ruleScopeRegex = ruleScopeRegex;
        this.targetAreaRegex = targetAreaRegex;
        this.targetAreaValueCaptureGroup = targetAreaValueCaptureGroup;
        this.ruleDescription = ruleDescription;
        this.ruleEnabled = ruleEnabled;
        this.ruleTargetMethod = ruleTargetMethod;
        this.autoTargetAutoGenerateName = autoTargetAutoGenerateName;
        this.customAutoTargetRegex = customAutoTargetRegex;
        this.customAutoTargetNameCaptureGroup = customAutoTargetNameCaptureGroup;
        this.customAutoTargetValueCaptureGroup = customAutoTargetValueCaptureGroup;
        compileRegexes();
    }

    private void compileRegexes() {
        // Rule scope
        try {
            ruleScopeRegexPattern = Pattern.compile(this.ruleScopeRegex,Pattern.DOTALL|Pattern.MULTILINE);
        }
        catch ( PatternSyntaxException e) {
            Logger.log("ERROR","Could not compile rule scope regex");
        }
        // Target area
        try {
            if (RegexUtil.getMatchGroupCount(this.targetAreaRegex) <= targetAreaValueCaptureGroup) {
                targetAreaRegexPattern = Pattern.compile(this.targetAreaRegex,Pattern.DOTALL|Pattern.MULTILINE);
            }
        }
        catch ( PatternSyntaxException e) {
            Logger.log("ERROR","Could not compile target area regex");
        }
        // Custom auto target
        try {
            if ( customAutoTargetRegex != null ) {
                customAutoTargetRegexPattern = Pattern.compile(this.customAutoTargetRegex,Pattern.DOTALL|Pattern.MULTILINE);
            }
        }
        catch ( PatternSyntaxException e) {
            Logger.log("ERROR","Could not compile custom auto target regex");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleScopeRegex() {
        return ruleScopeRegex;
    }

    public void setRuleScopeRegex(String ruleScopeRegex) {
        this.ruleScopeRegex = ruleScopeRegex;
        compileRegexes();
    }

    public String getTargetAreaRegex() {
        return targetAreaRegex;
    }

    public void setTargetAreaRegex(String targetAreaRegex) {
        this.targetAreaRegex = targetAreaRegex;
        compileRegexes();
    }

    public int getTargetAreaValueCaptureGroup() {
        return targetAreaValueCaptureGroup;
    }

    public void setTargetAreaValueCaptureGroup(int targetAreaValueCaptureGroup) {
        this.targetAreaValueCaptureGroup = targetAreaValueCaptureGroup;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public boolean isRuleEnabled() {
        return ruleEnabled;
    }

    public void setRuleEnabled(boolean ruleEnabled) {
        this.ruleEnabled = ruleEnabled;
    }

    public TargetingMethod getRuleTargetMethod() {
        return ruleTargetMethod;
    }

    public void setRuleTargetMethod(TargetingMethod ruleTargetMethod) {
        this.ruleTargetMethod = ruleTargetMethod;
    }

    public boolean isAutoTargetAutoGenerateName() {
        return autoTargetAutoGenerateName;
    }

    public void setAutoTargetAutoGenerateName(boolean autoTargetAutoGenerateName) {
        this.autoTargetAutoGenerateName = autoTargetAutoGenerateName;
    }


    public String getCustomAutoTargetRegex() {
        return customAutoTargetRegex;
    }

    public void setCustomAutoTargetRegex(String customAutoTargetRegex) {
        this.customAutoTargetRegex = customAutoTargetRegex;
        compileRegexes();
    }

    public int getCustomAutoTargetNameCaptureGroup() {
        return customAutoTargetNameCaptureGroup;
    }

    public void setCustomAutoTargetNameCaptureGroup(int customAutoTargetNameCaptureGroup) {
        this.customAutoTargetNameCaptureGroup = customAutoTargetNameCaptureGroup;
    }

    public int getCustomAutoTargetValueCaptureGroup() {
        return customAutoTargetValueCaptureGroup;
    }

    public void setCustomAutoTargetValueCaptureGroup(int customAutoTargetValueCaptureGroup) {
        this.customAutoTargetValueCaptureGroup = customAutoTargetValueCaptureGroup;
    }

    public ArrayList<InjectorTarget> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<InjectorTarget> targets) {
        this.targets = targets;
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

    public String getPayloadProcessScript() {
        return payloadProcessScript;
    }

    public void setPayloadProcessScript(String payloadProcessScript) {
        this.payloadProcessScript = payloadProcessScript;
    }

    public Pattern getTargetAreaRegexPattern() {
        return targetAreaRegexPattern;
    }

    public Pattern getRuleScopeRegexPattern() {
        return ruleScopeRegexPattern;
    }

    public Pattern getCustomAutoTargetRegexPattern() {
        return customAutoTargetRegexPattern;
    }

    public void setCustomAutoTargetRegexPattern(Pattern customAutoTargetRegexPattern) {
        this.customAutoTargetRegexPattern = customAutoTargetRegexPattern;
    }

    public InjectorRuleExport toInjectorRuleExport() {
        InjectorRuleExport injectorRuleExport = new InjectorRuleExport();
        injectorRuleExport.id = id;
        injectorRuleExport.name = name;
        injectorRuleExport.ruleScopeRegex = ruleScopeRegex;
        injectorRuleExport.targetAreaRegex = targetAreaRegex;
        injectorRuleExport.targetAreaValueCaptureGroup = targetAreaValueCaptureGroup;
        injectorRuleExport.ruleDescription = ruleDescription;
        injectorRuleExport.ruleEnabled = ruleEnabled;
        injectorRuleExport.ruleTargetMethod = ruleTargetMethod;
        injectorRuleExport.autoTargetAutoGenerateName = autoTargetAutoGenerateName;
        injectorRuleExport.customAutoTargetRegex = customAutoTargetRegex;
        injectorRuleExport.customAutoTargetNameCaptureGroup = customAutoTargetNameCaptureGroup;
        injectorRuleExport.customAutoTargetValueCaptureGroup = customAutoTargetValueCaptureGroup;


        injectorRuleExport.decodeScript = decodeScript;
        injectorRuleExport.encodeScript = encodeScript;
        injectorRuleExport.payloadProcessScript = payloadProcessScript;

        injectorRuleExport.targets = new InjectorTargetExport[targets.size()];
        for ( int i = 0; i < targets.size(); i++ ) {
            injectorRuleExport.targets[i] = targets.get(i).toInjectorTargetExport();
        }
        return injectorRuleExport;
    }

    /*
        Scan insertion logic
     */
    public boolean isApplicable(HttpRequest request) {
        if (isRuleEnabled()) {
            if ( getTargetAreaRegexPattern() != null && getRuleScopeRegexPattern() != null) {
                // Check if the rule matches the scope and the target area
                if ( RegexUtil.checkMatch(getTargetAreaRegexPattern(), request.toString()) && RegexUtil.checkMatch(getRuleScopeRegexPattern(), request.toString()) ) {
                    // Check if the target area is even present
                    String targetArea = InjectorUtil.extractTargetArea(request.toString(),getTargetAreaRegexPattern(),getTargetAreaValueCaptureGroup());
                    if ( targetArea != null && !targetArea.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void cancelBuildInsertionPoints() {
        if ( insertionPointBuilderWorker != null ) {
            insertionPointBuilderWorker.terminate();
        }
    }

    public ArrayList<InjectorInsertionPoint> getInsertionPointsCancellable(HttpRequest request) {
        ArrayList<InjectorInsertionPoint> insertionPoints = new ArrayList<InjectorInsertionPoint>();
        insertionPointBuilderWorker = new InsertionPointBuilderWorker(request);
        insertionPointBuilderThread = new Thread(insertionPointBuilderWorker);
        insertionPointBuilderThread.start();
        while ( insertionPointBuilderThread.isAlive() ) {
            try {
                insertionPointBuilderThread.join(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        if ( insertionPointBuilderWorker != null ) {
            insertionPoints.addAll(insertionPointBuilderWorker.getInsertionPoints());
            insertionPointBuilderWorker = null;
            insertionPointBuilderThread = null;
        }
        return insertionPoints;
    }

    public ArrayList<InjectorInsertionPoint> getInsertionPoints(HttpRequest request) {
        ArrayList<InjectorInsertionPoint> insertionPoints = new ArrayList<InjectorInsertionPoint>();
        InsertionPointBuilderWorker insertionPointBuilderWorker = new InsertionPointBuilderWorker(request);
        Thread insertionPointBuilderThread = new Thread(insertionPointBuilderWorker);
        insertionPointBuilderThread.start();
        try {
            insertionPointBuilderThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if ( insertionPointBuilderWorker != null ) {
            insertionPoints.addAll(insertionPointBuilderWorker.getInsertionPoints());
        }
        return insertionPoints;
    }

    class InsertionPointBuilderWorker implements Runnable {

        private HttpRequest request = null;
        private ArrayList<InjectorInsertionPoint> insertionPoints = new ArrayList<InjectorInsertionPoint>();
        private PythonScript decodePythonScript = null;

        public InsertionPointBuilderWorker( HttpRequest request ) {
            this.request = request;
        }

        public void terminate() {
            if (decodePythonScript != null) {
                try {
                    decodePythonScript.terminate();
                } catch (InterruptedException e) {
                    ;
                }
            }
        }



        public ArrayList<InjectorInsertionPoint> getInsertionPoints() {
            return insertionPoints;
        }

        @Override
        public void run() {
            buildInsertionPoints();
        }

        public void buildInsertionPoints() {
            if (isApplicable(request)) {
                decodePythonScript = new PythonScript(ScriptType.DECODE, getDecodeScript(), InjectorUtil.extractTargetArea(request.toString(), getTargetAreaRegexPattern(), getTargetAreaValueCaptureGroup()));
                decodePythonScript.execute();
                if (!decodePythonScript.hasErrors()) {
                    try {
                        int matchNumber = 1;
                        switch (getRuleTargetMethod()) {
                            case AUTO:
                                Logger.log("INFO", String.format("Target method for rule %s is %s", getName(), getRuleTargetMethod().toString()));
                                String targetArea = decodePythonScript.getStdout();
                                AutoTargeter autoTargeter = new AutoTargeter(targetArea);
                                Pattern autoRegexPattern = autoTargeter.getTargetPattern();
                                int autoNameCaptureGroup = -1;
                                int autoValueCaptureGroup = autoTargeter.getValueCaptureGroup();
                                if (autoRegexPattern != null) {
                                    Matcher autoMatcher = autoRegexPattern.matcher(targetArea);
                                    while (autoMatcher.find()) {
                                        InjectorInsertionPoint injectorInsertionPoint = new InjectorInsertionPoint(
                                                request,
                                                getEncodeScript(),
                                                getDecodeScript(),
                                                getPayloadProcessScript(),
                                                getTargetAreaRegexPattern(),
                                                getTargetAreaValueCaptureGroup(),
                                                getName(),
                                                String.format("%s_%s_%s", getName(), autoTargeter.getAutoTargetMatchFormat(), matchNumber),
                                                autoRegexPattern,
                                                autoNameCaptureGroup,
                                                autoValueCaptureGroup,
                                                matchNumber
                                        );
                                        insertionPoints.add(injectorInsertionPoint);
                                        matchNumber++;
                                    }
                                    Logger.log("INFO", String.format("Auto target matched %d values in [%s]", matchNumber, targetArea));
                                } else {
                                    Logger.log("ERROR", "Auto target match regex is null");
                                }
                                break;
                            case CUSTOM_AUTO:
                                Matcher customAutoMatcher = getCustomAutoTargetRegexPattern().matcher(decodePythonScript.getStdout());
                                while (customAutoMatcher.find()) {
                                    InjectorInsertionPoint injectorInsertionPoint = new InjectorInsertionPoint(
                                            request,
                                            getEncodeScript(),
                                            getDecodeScript(),
                                            getPayloadProcessScript(),
                                            getTargetAreaRegexPattern(),
                                            getTargetAreaValueCaptureGroup(),
                                            getName(),
                                            null,
                                            getCustomAutoTargetRegexPattern(),
                                            isAutoTargetAutoGenerateName() ? -1 : getCustomAutoTargetNameCaptureGroup(),
                                            getCustomAutoTargetValueCaptureGroup(),
                                            matchNumber
                                    );
                                    insertionPoints.add(injectorInsertionPoint);
                                    matchNumber++;
                                }
                                break;
                            case REGEX:
                                for (InjectorTarget target : targets) {
                                    Matcher targetMatcher = target.getTargetRegexPattern().matcher(decodePythonScript.getStdout());
                                    if (targetMatcher.find()) {
                                        InjectorInsertionPoint injectorInsertionPoint = new InjectorInsertionPoint(
                                                request,
                                                getEncodeScript(),
                                                getDecodeScript(),
                                                getPayloadProcessScript(),
                                                getTargetAreaRegexPattern(),
                                                getTargetAreaValueCaptureGroup(),
                                                getName(),
                                                target.getTargetName(),
                                                target.getTargetRegexPattern(),
                                                -1,
                                                target.getTargetValueCaptureGroup(),
                                                matchNumber
                                        );
                                        insertionPoints.add(injectorInsertionPoint);
                                    }
                                }
                                break;
                        }
                    } catch (InsertionPointException e) {
                        Logger.log("ERROR", String.format("Error creating insertion point - %s", e.getMessage()));
                    }
                }
            }
        }
    }
}
