package burp_injector.threads;

import burp.api.montoya.http.RequestOptions;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp_injector.util.MontoyaUtil;
import burp_injector.util.RequestUtil;

/**
 * Used to run an individual request
 */
public class RequestRunner implements Runnable {
    private MontoyaUtil montoyaUtil = MontoyaUtil.getInstance();
    private HttpRequest testRequest;

    public RequestRunner(HttpRequest testRequest) {
        this.testRequest = testRequest;
    }

    @Override
    public void run() {
        testRequest = RequestUtil.adjustContentLengthHeader(testRequest);
        montoyaUtil.getApi().http().sendRequest(testRequest, RequestOptions.requestOptions().withResponseTimeout(30000));
    }
}
