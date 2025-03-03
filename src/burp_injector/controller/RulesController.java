package burp_injector.controller;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPointProvider;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp_injector.enums.EditorState;
import burp_injector.enums.TargetingMethod;
import burp_injector.event.controller.RulesControllerEvent;
import burp_injector.insertionpoint.InjectorInsertionPoint;
import burp_injector.insertionpoint.InsertionPointException;
import burp_injector.model.RulesModel;
import burp_injector.model.data.InjectorRule;
import burp_injector.model.data.TestRequest;
import burp_injector.mvc.AbstractController;
import burp_injector.util.Logger;
import burp_injector.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Rules controller
 */
public class RulesController extends AbstractController<RulesControllerEvent, RulesModel> implements AuditInsertionPointProvider, ContextMenuItemsProvider {

    public RulesController(RulesModel model) {
        super(model);
    }
    @Override
    protected void handleEvent(RulesControllerEvent event, Object previous, Object next) {
        switch ( event ) {
            case RULES_ROW_SELECTION_UPDATE:
                if ( (Integer) next >= 0 ) {
                    getModel().loadRule(UIUtil.getIdByRowNumber(getModel().getInjectorRulesTableModel(), (Integer) next));
                }
                break;
            case CANCEL:
                if ( getModel().getLastRuleId() == null ) {
                    getModel().resetRule();
                    getModel().setRuleEditorState(EditorState.INITIAL);
                }
                else {
                    getModel().loadRule(getModel().getLastRuleId());
                }
                break;
            case DELETE:
                getModel().deleteRule();
                break;
            case SAVE:
                getModel().saveRule();
                break;
            case NEW:
                getModel().newRule();
                getModel().setRuleEditorState(EditorState.CREATE);
                break;
            case CUSTOM_AUTO_AUTONAME_UPDATED:
                getModel().setCustomAutoTargetAutoGenerateName((boolean) next);
                break;
            case CUSTOM_AUTO_VALUE_CAPTURE_GROUP_UPDATED:
                getModel().setCustomAutoTargetValueCaptureGroup((Integer)next);
                break;
            case CUSTOM_AUTO_NAME_CAPTURE_GROUP_UPDATED:
                getModel().setCustomAutoTargetNameCaptureGroup((Integer)next);
                break;
            case CUSTOM_AUTO_REGEX_UPDATED:
                getModel().setCustomAutoTargetRegex((String)next);
                break;
            case TARGET_METHOD_REGEX_SET:
                getModel().setRuleTargetingMethod(TargetingMethod.REGEX);
                break;
            case TARGET_METHOD_CUSTOM_AUTO_SET:
                getModel().setRuleTargetingMethod(TargetingMethod.CUSTOM_AUTO);
                break;
            case TARGET_METHOD_AUTO_SET:
                getModel().setRuleTargetingMethod(TargetingMethod.AUTO);
                break;
            case ENABLED_UPDATED:
                getModel().setRuleEnabled((boolean)next);
                break;
            case TARGET_AREA_CAPTURE_GROUP_UPDATED:
                getModel().setTargetAreaValueCaptureGroup((Integer)next);
                break;
            case TARGET_AREA_REGEX_UPDATED:
                getModel().setTargetAreaRegex((String)next);
                break;
            case SCOPE_UPDATED:
                getModel().setRuleScopeRegex((String)next);
                break;
            case DESCRIPTION_UPDATED:
                getModel().setRuleDescription((String)next);
                break;
            case NAME_UPDATED:
                getModel().setRuleName((String)next);
                break;
            case RULES_TABLE_MODEL_UPDATE:
                break;
            case TEST:
                getModel().testRule(getModel().getRuleId());
                break;
            case CANCEL_TEST:
                getModel().cancelRuleTest();
                break;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(RulesControllerEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }
    /*
        Injector logic
     */

    private ArrayList<InjectorRule> getApplicableRules(HttpRequest httpRequest) {
        ArrayList<InjectorRule> applicableRules = new ArrayList<InjectorRule>();
        for ( InjectorRule injectorRule : getModel().getInjectorRules() ) {
            if (!injectorRule.isRuleEnabled()) {
                continue;
            }
            if ( injectorRule.getRuleScopeRegex() != null ) {
                Matcher m = injectorRule.getTargetAreaRegexPattern().matcher(httpRequest.toString());
                if( m.find() ) {
                    applicableRules.add(injectorRule);
                }
            }
        }
        return applicableRules;
    }

    @Override
    public List<AuditInsertionPoint> provideInsertionPoints(HttpRequestResponse baseHttpRequestResponse) {
        ArrayList<AuditInsertionPoint> auditInsertionPoints = new ArrayList<AuditInsertionPoint>();
        for ( InjectorRule injectorRule : getApplicableRules(baseHttpRequestResponse.request())) {
            for ( InjectorInsertionPoint injectorInsertionPoint : injectorRule.getInsertionPoints(baseHttpRequestResponse.request())) {
                try {
                    injectorInsertionPoint.init();
                    auditInsertionPoints.add(injectorInsertionPoint);
                } catch (InsertionPointException e) {
                    Logger.log("ERROR", String.format("Could not initalize rule - %s", e.getMessage()));
                }
            }
        }
        Logger.log("INFO", String.format("Returned %d insertion points", auditInsertionPoints.size()));
        return auditInsertionPoints;
    }

    // Context menu for add request on PROXY, TARGET and LOGGER
    @Override
    public java.util.List<Component> provideMenuItems(ContextMenuEvent event)
    {
        List<Component> menuItemList = new ArrayList<>();
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.LOGGER, ToolType.REPEATER)) {
            if ( event.selectedRequestResponses().size() == 1 || event.messageEditorRequestResponse().isPresent() ) {
                JMenuItem mnuSendToInjector = new JMenuItem("Send to Injector");
                mnuSendToInjector.addActionListener(actionEvent -> {
                    HttpRequestResponse requestResponse = null;
                    if ( event.messageEditorRequestResponse().isPresent() ) {
                        requestResponse = event.messageEditorRequestResponse().get().requestResponse();
                    }
                    else {
                        requestResponse = event.selectedRequestResponses().get(0);
                    }

                    if ( requestResponse != null ) {
                        TestRequest testRequest = new TestRequest(
                                requestResponse.request().toString(),
                                requestResponse.request().httpService().toString()
                        );
                        getModel().getTestRequestModel().updateTestRequest(getModel().getRuleId(),testRequest);
                    }
                });
                menuItemList.add(mnuSendToInjector);
            }
        }
        return menuItemList;
    }
}
