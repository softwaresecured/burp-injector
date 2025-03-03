package burp_injector.model.data;

public class TestRequest {
    private String testReqeust;
    private String baseURL;

    public TestRequest(String testReqeust, String baseURL) {
        this.testReqeust = testReqeust;
        this.baseURL = baseURL;
    }

    public String getTestReqeust() {
        return testReqeust;
    }

    public void setTestReqeust(String testReqeust) {
        this.testReqeust = testReqeust;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
