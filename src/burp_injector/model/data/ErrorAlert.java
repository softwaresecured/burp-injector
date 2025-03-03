package burp_injector.model.data;

public class ErrorAlert {
    private String alertTitle;
    private String alertMessage;

    public ErrorAlert(String alertTitle, String alertMessage) {
        this.alertTitle = alertTitle;
        this.alertMessage = alertMessage;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public String getAlertMessage() {
        return alertMessage;
    }
}
