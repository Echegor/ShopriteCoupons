package com.archelo.coupons;

public class LoginStatus {
    private String UserId;
    private boolean isSignedIn;
    public LoginStatus(String userId, boolean isSignedIn){
        this.UserId = userId;
        this.isSignedIn = isSignedIn;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public boolean isSignedIn() {
        return isSignedIn;
    }

    public void setSignedIn(boolean signedIn) {
        isSignedIn = signedIn;
    }

    public String toString(){
        return "{UserId:" + UserId + ":" + "IsSignedIn:" + isSignedIn+"}";
    }
}
