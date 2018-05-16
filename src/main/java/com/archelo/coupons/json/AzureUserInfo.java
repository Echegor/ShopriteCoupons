package com.archelo.coupons.json;

public class AzureUserInfo {
    private String SSOUrl;
    private String Active;
    private String Message;
    private UserInfo UserInfo;

    public AzureUserInfo(String SSOUrl, String active, String message, UserInfo userInfo) {
        this.SSOUrl = SSOUrl;
        Active = active;
        Message = message;
        UserInfo = userInfo;
    }

    public String getSSOUrl() {
        return SSOUrl;
    }

    public String getActive() {
        return Active;
    }

    public String getMessage() {
        return Message;
    }

    public UserInfo getUserInfo() {
        return UserInfo;
    }

    @Override
    public String toString() {
        return "AzureUserInfo{" +
                "SSOUrl='" + SSOUrl + '\'' +
                ", Active='" + Active + '\'' +
                ", Message='" + Message + '\'' +
                ", UserInfo=" + UserInfo +
                '}';
    }
}
