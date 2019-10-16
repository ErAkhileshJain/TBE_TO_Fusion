package ui;

public class URLConfiguration {
    private String taleoUrl;
    private String taleoUserName;
    private String taleoPassword;

    // Credential for HCM Fusion
    private String hcmUrlString;
    private String hcmUserName;
    private String hcmPassword;
    
    //Mail ID's
    private String toRecipient;
    private String ccRecipient;
    private String fromMailId;
    private String fromMailPassword;
    private String smtpHost;
    private String smtpPort;
    
    //Search Status
    private String status;


    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTaleoUrl(String taleoUrl) {
        this.taleoUrl = taleoUrl;
    }

    public String getTaleoUrl() {
        return taleoUrl;
    }

    public void setTaleoUserName(String taleoUserName) {
        this.taleoUserName = taleoUserName;
    }

    public String getTaleoUserName() {
        return taleoUserName;
    }

    public void setTaleoPassword(String taleoPassword) {
        this.taleoPassword = taleoPassword;
    }

    public String getTaleoPassword() {
        return taleoPassword;
    }

    public void setHcmUrlString(String hcmUrlString) {
        this.hcmUrlString = hcmUrlString;
    }

    public String getHcmUrlString() {
        return hcmUrlString;
    }

    public void setHcmUserName(String hcmUserName) {
        this.hcmUserName = hcmUserName;
    }

    public String getHcmUserName() {
        return hcmUserName;
    }

    public void setHcmPassword(String hcmPassword) {
        this.hcmPassword = hcmPassword;
    }

    public String getHcmPassword() {
        return hcmPassword;
    }


    public void setToRecipient(String toRecipient) {
        this.toRecipient = toRecipient;
    }

    public String getToRecipient() {
        return toRecipient;
    }

    public void setCcRecipient(String ccRecipient) {
        this.ccRecipient = ccRecipient;
    }

    public String getCcRecipient() {
        return ccRecipient;
    }

    public void setFromMailId(String fromMailId) {
        this.fromMailId = fromMailId;
    }

    public String getFromMailId() {
        return fromMailId;
    }

    public void setFromMailPassword(String fromMailPassword) {
        this.fromMailPassword = fromMailPassword;
    }

    public String getFromMailPassword() {
        return fromMailPassword;
    }


    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

}
