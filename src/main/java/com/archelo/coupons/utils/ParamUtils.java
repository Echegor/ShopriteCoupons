package com.archelo.coupons.utils;

import com.archelo.coupons.json.AzureToken;
import com.archelo.coupons.json.AzureUserInfo;
import com.archelo.coupons.json.LoginStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class ParamUtils {
    public static String getRequestVerificationToken(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=__RequestVerificationToken]").first().val();
    }

    public static String getSamlRequestForm(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=SAMLRequest]").first().val();
    }

    public static String getSamlResponseForm(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=SAMLResponse]").first().val();
    }

    public static String getRelayState(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=RelayState]").first().val();
    }

    public static String getTokenValue(String couponHome) {
        String apiScript = "scripts/web.js?t=";
        int index = couponHome.indexOf(apiScript);
        int finalIndex = couponHome.indexOf("\"", index);
        return couponHome.substring(index + apiScript.length(), finalIndex);
    }

    public static AzureToken buildAzureToken(String webJSFile, String status) {
        LoginStatus loginStatus = new Gson().fromJson(status, LoginStatus.class);
        int index = webJSFile.indexOf(AzureToken.AUTHORIZATION);
        int endIndex = webJSFile.indexOf("&", index);
        String authorization = webJSFile.substring(index + 14, endIndex);

        index = webJSFile.indexOf(AzureToken.ZUMO_APPLICATION_TOKEN);
        endIndex = webJSFile.indexOf("\"", index + AzureToken.ZUMO_APPLICATION_TOKEN.length());
        String zumoApplicationToken = webJSFile.substring(index + AzureToken.ZUMO_APPLICATION_TOKEN.length(), endIndex);

        return new AzureToken.AzureTokenBuilder()
                .authorization(authorization)
                .userID(loginStatus.getUserId())
                .zumoApplicationToken(zumoApplicationToken)
                .signInStatus(loginStatus.isSignedIn())
                .build();
    }


    public static List<NameValuePair> buildAzureUserSignInQueryParams(AzureToken azureTokens) {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("sessId", azureTokens.getUserID()));
        list.add(new BasicNameValuePair("returnUrl", "http://coupons.shoprite.com/"));
        return list;
    }

    public static List<NameValuePair> getAzureSession(AzureToken azureTokens) {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair(AzureToken.AUTHORIZATION, azureTokens.getAuthorization()));
        list.add(new BasicNameValuePair("returnUrl", "http://coupons.shoprite.com/"));
        return list;
    }

    public static List<NameValuePair> buildSamlRequestQueryParameters() {
        ArrayList<NameValuePair> samlQueryParams = new ArrayList<>();
        samlQueryParams.add(new BasicNameValuePair("binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
        samlQueryParams.add(new BasicNameValuePair("cancelUri", "https://shop.shoprite.com/store/DFA0738/User/QuickSignIn?success=False"));
        return samlQueryParams;
    }

    public static List<NameValuePair> buildAzureSamlRequestQueryParameters() {
        ArrayList<NameValuePair> samlQueryParams = new ArrayList<>();
        samlQueryParams.add(new BasicNameValuePair("binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
        samlQueryParams.add(new BasicNameValuePair("cancelUri", "cancelUri=https://wfsso.azurewebsites.net/SRSSO/CancelSignIn/store/027F776?success=False"));
        return samlQueryParams;
    }

    public static List<NameValuePair> buildSamlRequestDataParameters(String saml) {
        String samlForm = ParamUtils.getSamlRequestForm(saml);
        String relayState = ParamUtils.getRelayState(saml);

        ArrayList<NameValuePair> samlQueryData = new ArrayList<>();
        samlQueryData.add(new BasicNameValuePair("SAMLRequest", samlForm));
        samlQueryData.add(new BasicNameValuePair("RelayState", relayState));
        return samlQueryData;
    }

    public static List<NameValuePair> buildSamlResponseDataParameter(String saml) {
        ArrayList<NameValuePair> samlResponseData = new ArrayList<>();
        samlResponseData.add(new BasicNameValuePair("SAMLResponse", ParamUtils.getSamlResponseForm(saml)));
        samlResponseData.add(new BasicNameValuePair("RelayState", ParamUtils.getRelayState(saml)));
        return samlResponseData;
    }

    public static List<NameValuePair> getCouponHome(String coupons) {
        ArrayList<NameValuePair> samlResponseData = new ArrayList<>();
        samlResponseData.add(new BasicNameValuePair("t", getTokenValue(coupons)));
        return samlResponseData;
    }

    public static List<NameValuePair> getAzureSignInStatus(String status) {
        LoginStatus loginStatus = new Gson().fromJson(status, LoginStatus.class);
        ArrayList<NameValuePair> samlQueryParams = new ArrayList<>();
        samlQueryParams.add(new BasicNameValuePair("sessId", loginStatus.getUserId()));
        samlQueryParams.add(new BasicNameValuePair("returnUrl", "http://coupons.shoprite.com/"));
        return samlQueryParams;
    }

    public static List<NameValuePair> buildAzureStatusQueryParam(AzureToken azureTokens) {
        ArrayList<NameValuePair> samlQueryParams = new ArrayList<>();
        samlQueryParams.add(new BasicNameValuePair("Authorization", azureTokens.getAuthorization()));
        samlQueryParams.add(new BasicNameValuePair("returnUrl", "http://coupons.shoprite.com/"));
        return samlQueryParams;
    }

    public static JsonObject buildPPCJSON(AzureUserInfo userInfo) {
        JsonObject object = new JsonObject();
        object.addProperty("ppc_number", userInfo.getUserInfo().getFSN());
        return object;
    }

    public static JsonObject buildCouponJSON(String coupon, AzureUserInfo userInfo) {
        JsonObject object = new JsonObject();
        object.addProperty("ppc_number", userInfo.getUserInfo().getFSN());
        object.addProperty("clip_source", "Web_SR");
        object.addProperty("coupon_id", coupon);
        return object;
    }
}
