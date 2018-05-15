package com.archelo.coupons;

public class AzureTokens {

    private String authorization;

    public AzureTokens(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String toString(){
        return "Authorization="+authorization;
    }

}
