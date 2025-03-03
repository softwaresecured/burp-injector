package burp_injector.view;

import burp_injector.event.controller.TestRequestControllerEvent;
import burp_injector.event.model.RulesModelEvent;
import burp_injector.event.model.TestRequestModelEvent;
import burp_injector.model.RulesModel;
import burp_injector.model.TestRequestModel;
import burp_injector.mvc.AbstractView;
import burp_injector.ui.PnlTestRequest;
import burp_injector.util.UIUtil;

import java.beans.PropertyChangeEvent;

/**
 * Test request view
 */
public class TestRequestView extends AbstractView<TestRequestControllerEvent, TestRequestModel, TestRequestModelEvent> {

    private RulesModel rulesModel;

    public PnlTestRequest pnlTestRequest = new PnlTestRequest();


    public TestRequestView(TestRequestModel testRequestModel, RulesModel rulesModel) {
        super(testRequestModel);
        this.rulesModel = rulesModel;
    }

    @Override
    public void attachListeners() {

    }


    protected void handleEvent(RulesModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case TARGET_AREA_CAPTURE_GROUP_CHANGED:
            case TARGET_AREA_REGEX_CHANGED:
                pnlTestRequest.testRequest.getHighlighter().removeAllHighlights();
                UIUtil.updateHighlighting(
                        rulesModel.getTargetAreaRegex(),
                        rulesModel.getTargetAreaValueCaptureGroup(),
                        pnlTestRequest.testRequest);
                break;
        }
    }

    @Override
    protected void handleEvent(TestRequestModelEvent event, Object previous, Object next) {
        switch ( event ) {
            case TEST_HTTP_REQUEST_CHANGED:
                pnlTestRequest.testRequest.setText(getModel().getTestHttpRequestStr());
                UIUtil.updateHighlighting(
                        rulesModel.getTargetAreaRegex(),
                        rulesModel.getTargetAreaValueCaptureGroup(),
                        pnlTestRequest.testRequest);
                break;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() instanceof TestRequestModel) {
            handleEvent(TestRequestModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
        if ( evt.getSource() instanceof RulesModel) {
            handleEvent(RulesModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
    }
}
