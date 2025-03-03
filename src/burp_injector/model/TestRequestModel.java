package burp_injector.model;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp_injector.config.AbstractConfig;
import burp_injector.config.InjectorRuleConfigExport;
import burp_injector.config.TestRequestConfigExport;
import burp_injector.config.TestRequestExport;
import burp_injector.enums.ConfigKey;
import burp_injector.event.model.TestRequestModelEvent;
import burp_injector.model.data.TestRequest;
import burp_injector.mvc.AbstractModel;
import burp_injector.util.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.HashMap;

/**
 * Test request model
 */
public class TestRequestModel extends AbstractModel<TestRequestModelEvent> {
    private String testHttpRequest = null;
    private String baseURL = null;
    private String currentRuleId = null;
    private HashMap<String, TestRequest> testRequestMap = new HashMap<String,TestRequest>();

    public TestRequestModel() {
        super();
    }

    @Override
    public void load(AbstractConfig config) {
        String testRequestMapJSON = config.getString(ConfigKey.TEST_HTTP_REQUEST_MAP, null);
        if ( testRequestMapJSON != null ) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                TestRequestConfigExport testRequestConfigExport = mapper.readValue(new String(testRequestMapJSON), TestRequestConfigExport.class);
                if ( testRequestConfigExport.testRequests != null ) {
                    for ( TestRequestExport testRequestExport : testRequestConfigExport.testRequests ) {
                        updateTestRequest(testRequestExport.id,new TestRequest(testRequestExport.testRequest,testRequestExport.baseURL));
                    }
                }
            } catch (JsonProcessingException e) {
                Logger.log("ERROR", String.format("Could not import test request cofniguration - %s", e.getMessage()));
            }
        }
    }

    @Override
    public void save(AbstractConfig config) {
        try {
            config.setString(ConfigKey.TEST_HTTP_REQUEST_MAP, exportTestRequestsAsJSON());
        } catch (JsonProcessingException e) {
            Logger.log("ERROR", String.format("Could not export test request configuration - %s", e.getMessage()));
        }
    }

    public String getTestHttpRequestStr() {
        return testHttpRequest;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        var old = this.baseURL;
        this.baseURL = baseURL;
        emit(TestRequestModelEvent.TEST_HTTP_REQUEST_BASE_URL_CHANGED, old, baseURL);
    }

    public void setTestHttpRequest(String testHttpRequest) {
        var old = this.testHttpRequest;
        this.testHttpRequest = testHttpRequest;
        emit(TestRequestModelEvent.TEST_HTTP_REQUEST_CHANGED, old, testHttpRequest);
    }

    public HttpRequest getHttpRequest() {
        HttpRequest request = null;
        if ( testHttpRequest != null && baseURL != null ) {
            request = HttpRequest.httpRequest(testHttpRequest).withService(HttpService.httpService(baseURL));
            if ( request.httpVersion().endsWith("2")) {
                request = HttpRequest.http2Request(request.httpService(),request.headers(),request.body()).withService(HttpService.httpService(baseURL));
            }
        }
        return request;
    }

    public String getCurrentRuleId() {
        return currentRuleId;
    }

    public void setCurrentRuleId(String currentRuleId) {
        this.currentRuleId = currentRuleId;
    }

    public void loadTestRequest(String ruleId ) {
        TestRequest testRequest = getTestRequestByRuleId(ruleId);
        if ( testRequest != null ) {
            setTestHttpRequest(testRequest.getTestReqeust());
            setBaseURL(testRequest.getBaseURL());
            setCurrentRuleId(ruleId);
        }
        else {
            setTestHttpRequest(null);
            setBaseURL(null);
            setCurrentRuleId(null);
        }
    }

    public TestRequest getTestRequestByRuleId( String ruleId ) {
        return testRequestMap.get(ruleId);
    }

    public void updateTestRequest( String id, TestRequest testRequest ) {
        if ( id == null ) {
            setTestHttpRequest(testRequest.getTestReqeust());
            setBaseURL(testRequest.getBaseURL());
        }
        else {
            testRequestMap.put(id,testRequest);
            loadTestRequest(id);
        }
    }

    public void removeTestRequest( String id ) {
        testRequestMap.remove(id);
        if ( getCurrentRuleId() != null ) {
            if (id.equals(currentRuleId)) {
                setCurrentRuleId(null);
            }
        }
    }

    public String exportTestRequestsAsJSON() throws JsonProcessingException {
        TestRequestConfigExport testRequestConfigExport = new TestRequestConfigExport();
        testRequestConfigExport.testRequests = new TestRequestExport[testRequestMap.size()];
        int i = 0;
        for (var entry : testRequestMap.entrySet()) {
            TestRequest testRequest = entry.getValue();
            TestRequestExport testRequestExport = new TestRequestExport();
            testRequestExport.id = entry.getKey();
            testRequestExport.testRequest = testRequest.getTestReqeust();
            testRequestExport.baseURL = testRequest.getBaseURL();
            testRequestConfigExport.testRequests[i] = testRequestExport;
            i++;
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(testRequestConfigExport);
    }
}
