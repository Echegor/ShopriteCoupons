package com.archelo.coupons.json;

public class AzureToken {
    public final static String AUTHORIZATION = "Authorization";
    public final static String ZUMO_APPLICATION_TOKEN = "\"X-ZUMO-APPLICATION\":\"";
    private final String authorization;
    private final String userID;
    private final String zumoApplicationToken;
    private final boolean isSignedIn;

    private AzureToken(AzureTokenBuilder builder) {
        this.authorization = builder.authorization;
        this.userID = builder.userID;
        this.zumoApplicationToken = builder.zumoApplicationToken;
        this.isSignedIn = builder.isSignedIn;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getUserID() {
        return userID;
    }

    public String getZumoApplicationToken() {
        return zumoApplicationToken;
    }

    public boolean isSignedIn() {
        return isSignedIn;
    }

    public String toString() {
        return "{ \"Authorization\" : \"" + authorization + "\", " +
                "\"userID\" : \"" + userID + "\", " +
                "\"isSignedIn\" : \"" + isSignedIn + "\", " +
                "\"zumoApplicationToken\" : \"" + zumoApplicationToken + "\"" +
                "}";
    }

    public static class AzureTokenBuilder {
        private String authorization;
        private String userID;
        private String zumoApplicationToken;
        private boolean isSignedIn;

        public AzureTokenBuilder() {
        }

        public AzureTokenBuilder authorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public AzureTokenBuilder userID(String userID) {
            this.userID = userID;
            return this;
        }

        public AzureTokenBuilder zumoApplicationToken(String zumoApplicationToken) {
            this.zumoApplicationToken = zumoApplicationToken;
            return this;
        }

        public AzureTokenBuilder signInStatus(boolean signedIn) {
            this.isSignedIn = signedIn;
            return this;
        }

        public AzureToken build() {
            return new AzureToken(this);
        }


    }
}
