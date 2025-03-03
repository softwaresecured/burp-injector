package burp_injector.controller;

import burp.api.montoya.MontoyaApi;
import burp_injector.event.controller.TestRequestControllerEvent;
import burp_injector.model.TestRequestModel;
import burp_injector.mvc.AbstractController;
import java.beans.PropertyChangeEvent;

/**
 * Test request controller
 */
public class TestRequestController extends AbstractController<TestRequestControllerEvent, TestRequestModel>  {

    public TestRequestController(TestRequestModel model) {
        super(model);
    }

    @Override
    protected void handleEvent(TestRequestControllerEvent event, Object previous, Object next) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        handleEvent(TestRequestControllerEvent.valueOf(event.getPropertyName()), event.getOldValue(), event.getNewValue());
    }
}
