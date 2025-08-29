package burp_injector;

import burp.VERSION;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolSource;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.api.montoya.ui.editor.extension.HttpRequestEditorProvider;
import burp.api.montoya.ui.swing.SwingUtils;
import burp_injector.config.MontoyaConfig;
import burp_injector.controller.*;
import burp_injector.editors.RequestEditor;
import burp_injector.enums.EditorState;
import burp_injector.model.*;
import burp_injector.mvc.AbstractModel;
import burp_injector.mvc.AbstractView;
import burp_injector.mvc.MVC;
import burp_injector.python.PythonScriptExecutorWatchdog;
import burp_injector.ui.InjectorConfigTab;
import burp_injector.util.Logger;
import burp_injector.util.MontoyaUtil;
import burp_injector.view.*;


public class BurpInjector implements BurpExtension, ExtensionUnloadingHandler, HttpRequestEditorProvider {
    public static final String EXTENSION_NAME = "Deep Data Injector";
    private MontoyaApi api;
    private InjectorConfigTab tab;
    private MVC<RulesModel, RulesView, RulesController> rules;
    private MVC<RegexTargetsModel, RegexTargetsView, RegexTargetsController> regexTargets;
    private MVC<ScriptModel, ScriptView, ScriptController> scripts;
    private MVC<TestRequestModel, TestRequestView, TestRequestController> testRequest;

    private MontoyaConfig config;

    @Override
    public void initialize(MontoyaApi api) {
        PythonScriptExecutorWatchdog.getInstance().startWatchDog();
        PythonScriptExecutorWatchdog.getInstance().setMaxProcessExecutionTimeSec(30);
        this.api = api;
        MontoyaUtil montoyaUtil = MontoyaUtil.getInstance();
        montoyaUtil.setMontoyaApi(api);
        api.extension().setName(EXTENSION_NAME);
        Logger.setLogger(api.logging());
        Logger.log("INFO", String.format("%s %s loaded", EXTENSION_NAME, VERSION.getVersionStr()));
        buildMVCs();
        this.config = new MontoyaConfig(api.persistence());

        rules.getModel().setRuleEditorState(EditorState.INITIAL);
        rules.getModel().setApi(api);
        this.tab = buildTab();
        api.userInterface().registerSuiteTab(EXTENSION_NAME, this.tab);
        for (AbstractModel<?> model : getModels()) {
            model.load(config);
        }
        if ( rules.getModel().getInjectorRules().size() > 0 ) {
            rules.getModel().loadRuleObject(rules.getModel().getInjectorRules().getLast());
        }
        api.extension().registerUnloadingHandler(this);
        api.userInterface().registerContextMenuItemsProvider(rules.getController());
        api.scanner().registerInsertionPointProvider(rules.getController());
        scripts.getView().pnlScriptOutput.jtxtSamplePayload.setText(scripts.getModel().getPayloadTest());
        api.userInterface().registerHttpRequestEditorProvider(this);
    }

    public InjectorConfigTab buildTab() {
        InjectorConfigTab tab = new InjectorConfigTab(
                rules.getView(),
                regexTargets.getView(),
                scripts.getView(),
                testRequest.getView()
        );

        for (AbstractView<?, ?, ?> view : getViews()) {
            view.attachListeners();
        }
        rules.getView().setParentComponent(tab);
        return tab;
    }

    private AbstractModel<?>[] getModels() {
        return new AbstractModel[] {
                rules.getModel(),
                regexTargets.getModel(),
                scripts.getModel(),
                testRequest.getModel()
        };
    }

    private AbstractView<?, ?, ?>[] getViews() {
        return new AbstractView[] {
                rules.getView(),
                regexTargets.getView(),
                scripts.getView(),
                testRequest.getView()

        };
    }

    public void buildMVCs() {

        RulesModel rulesModel = new RulesModel();
        this.rules = new MVC<>(rulesModel, new RulesView(rulesModel), new RulesController(rulesModel));

        RegexTargetsModel regexTargetsModel = new RegexTargetsModel();
        this.regexTargets = new MVC<>(regexTargetsModel, new RegexTargetsView(regexTargetsModel), new RegexTargetsController(regexTargetsModel));

        ScriptModel scriptModel = new ScriptModel();
        this.scripts = new MVC<>(scriptModel, new ScriptView(scriptModel), new ScriptController(scriptModel));

        TestRequestModel testRequestModel = new TestRequestModel();
        this.testRequest = new MVC<>(testRequestModel, new TestRequestView(testRequestModel,rulesModel), new TestRequestController(testRequestModel));


        rules.getModel().addListener(this.testRequest.getView());
        rules.getModel().addListener(this.scripts.getView());
        rules.getModel().addListener(regexTargets.getView());
        regexTargets.getModel().addListener(scripts.getView());

        rules.getModel().setRegexTargetsModel(regexTargets.getModel());
        rules.getModel().setScriptsModel(scripts.getModel());
        rules.getModel().setTestRequestModel(testRequest.getModel());
        scripts.getModel().setRegexTargetsModel(regexTargets.getModel());
        scripts.getModel().setRulesModel(rules.getModel());
        rules.getView().setParentComponent(api.userInterface().swingUtils().suiteFrame());
        regexTargets.getView().setParentComponent(api.userInterface().swingUtils().suiteFrame());
        scripts.getView().setParentComponent(api.userInterface().swingUtils().suiteFrame());
    }

    @Override
    public void extensionUnloaded() {
        for (AbstractModel<?> model : getModels()) {
            model.save(config);
        }
        PythonScriptExecutorWatchdog.getInstance().stopWatchDog();
    }

    @Override
    public ExtensionProvidedHttpRequestEditor provideHttpRequestEditor(EditorCreationContext creationContext) {
        if ( creationContext.toolSource().isFromTool(ToolType.REPEATER) || creationContext.toolSource().isFromTool(ToolType.LOGGER) || creationContext.toolSource().isFromTool(ToolType.PROXY)) {
            RequestEditor requestEditor = new RequestEditor(rules.getModel());
            if (!creationContext.toolSource().isFromTool(ToolType.REPEATER)) {
                requestEditor.setReadOnly();
            }
            return requestEditor;
        }
        return null;
    }
}