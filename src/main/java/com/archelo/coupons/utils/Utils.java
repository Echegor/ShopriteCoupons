package com.archelo.coupons.utils;

import com.archelo.coupons.HTTPHandler;
import com.archelo.coupons.ShopriteURLS;
import com.archelo.coupons.json.AzureToken;
import com.archelo.coupons.json.AzureUserInfo;
import com.archelo.coupons.json.CouponsArray;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;

public class Utils {


    public static void writeToFile(String file, String entity) {
        try {
            Files.write(Paths.get("logs", file), entity.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String doPost(HTTPHandler handler, String url, String filename, List<Header> headers, List<NameValuePair> queryParam, List<NameValuePair> dataParams) {
        System.out.println(new Date() + " POSTING");
        HttpResponse response = handler.doURLEncodedPost(url, headers, queryParam, dataParams);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static String doURLUnencodedPost(HTTPHandler handler, String url, String filename, List<Header> headers, List<NameValuePair> queryParam, List<NameValuePair> dataParams) {
        System.out.println(new Date() + " POSTING");
        HttpResponse response = handler.doURLUnencodedPost(url, headers, queryParam, dataParams);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static String doJSONPost(HTTPHandler handler, String url, String filename, List<Header> headers, JsonObject dataParams) {
        System.out.println(new Date() + " POSTING");
        HttpResponse response = handler.doJsonEncodedPost(url, headers, dataParams);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static String doJSONPostNoFile(HTTPHandler handler, String url, List<Header> headers, JsonObject dataParams) {
        System.out.println(new Date() + " POSTING");
        HttpResponse response = handler.doJsonEncodedPost(url, headers, dataParams);
        String entity = handler.printResponse(response, System.out, false);
        return entity;
    }


    public static String doGet(HTTPHandler handler, String url, String filename, List<Header> headers) {
        System.out.println(new Date() + " GETTING");
        HttpResponse response = handler.doGet(url, headers);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static String doUrlUnencodedGet(HTTPHandler handler, String url, String filename, List<Header> headers, List<NameValuePair> queryParams) {
        System.out.println(new Date() + " GETTING");
        HttpResponse response = handler.doURLEncodedGet(url, headers, queryParams);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static String doUrlEncodedGet(HTTPHandler handler, String url, String filename, List<Header> headers, List<NameValuePair> queryParams) {
        System.out.println(new Date() + " GETTING");
        HttpResponse response = handler.doURLEncodedGet(url, headers, queryParams);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static void performShopRiteSignIn(HTTPHandler handler) {
        //fetch saml
        String SAMLRequest = Utils.doGet(handler, ShopriteURLS.QUICK_SIGN_IN, "SAMLRequest.html", HeaderUtils.getBasicHeaders());
        //post fetched saml
        String authenticate3601 = Utils.doPost(handler, ShopriteURLS.AUTHENTICATE3601, "AUTHENTICATE3601.html", HeaderUtils.getAuthenticationHeaders3601(), ParamUtils.buildSamlRequestQueryParameters(), ParamUtils.buildSamlRequestDataParameters(SAMLRequest));
        String postToken = ParamUtils.getRequestVerificationToken(authenticate3601);
        String SAMLResponse = Utils.doPost(handler, ShopriteURLS.AUTHENTICATE, "SAMLResponse.html", HeaderUtils.getAuthenticationHeaders(), null, HeaderUtils.getAuthenticationInfo(postToken));
        String signInSuccess = Utils.doPost(handler, ShopriteURLS.SIGN_IN_SUCCESS, "SIGN_IN_SUCCESS.html", HeaderUtils.getSignInSuccessHeaders(), null, ParamUtils.buildSamlResponseDataParameter(SAMLResponse));

        //performAzureSignIn(handler,SAMLResponse);
    }

    public static void performAzureSignIn(HTTPHandler handler) {
        String status = Utils.doGet(handler, ShopriteURLS.STATUS, "STATUS_COUPONS.html", HeaderUtils.getStatus());
        String samlRequest = Utils.doUrlUnencodedGet(handler, ShopriteURLS.AZURE_SIGN_IN, "AZURE_SIGN_IN.html", HeaderUtils.getAzureSignInStatus(), ParamUtils.getAzureSignInStatus(status));
        String authenticate3601 = Utils.doURLUnencodedPost(handler, ShopriteURLS.AUTHENTICATE3601, "AUTHENTICATE3601.html", HeaderUtils.getAzureAuthenticationHeaders3601(), ParamUtils.buildAzureSamlRequestQueryParameters(), ParamUtils.buildSamlRequestDataParameters(samlRequest));
        String postToken = ParamUtils.getRequestVerificationToken(authenticate3601);
        String SAMLResponse = Utils.doPost(handler, ShopriteURLS.AUTHENTICATE, "SAMLResponse.html", HeaderUtils.getAuthenticationHeaders(), null, HeaderUtils.getAuthenticationInfo(postToken));
        String returnFromSignin = Utils.doPost(handler, ShopriteURLS.AZURE_RETURN_FROM_SIGN_IN, "AZURE_RETURN_FROM_SIGN_IN.html", HeaderUtils.getReturnFromSignInHeaders(), null, ParamUtils.buildSamlResponseDataParameter(SAMLResponse));
        String webJS = Utils.doGet(handler, ShopriteURLS.WEB_JS, "WEB_JS.html", HeaderUtils.getWebJsHeaders());
        AzureToken azureTokens = ParamUtils.buildAzureToken(webJS, status);
        String azureInfo = Utils.doUrlUnencodedGet(handler, ShopriteURLS.AZURE_SESSION + azureTokens.getUserID(), "AZURE_STATUS.html", HeaderUtils.getAzureStatus(), ParamUtils.buildAzureStatusQueryParam(azureTokens));
        AzureUserInfo userInfo = new Gson().fromJson(azureInfo, AzureUserInfo.class);
        String avaiableCoupons = doJSONPost(handler, ShopriteURLS.AZURE_AVAILABLE_COUPONS, "AZURE_AVAILABLE_COUPONS.json", HeaderUtils.getAvailableCoupons(azureTokens), ParamUtils.buildPPCJSON(userInfo));
        CouponsArray couponsArray = new Gson().fromJson(avaiableCoupons, CouponsArray.class);
        System.out.println(couponsArray);

        List<Header> couponHeader = HeaderUtils.getAvailableCoupons(azureTokens);
        for (String coupon : couponsArray.getAvailable_ids_array()) {
            String response = doJSONPostNoFile(handler, ShopriteURLS.AZURE_COUPONS_ADD, couponHeader, ParamUtils.buildCouponJSON(coupon, userInfo));
            System.out.println("Response: " + response);
        }
    }

    public static void setDebugProperties() {
//        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http","DEBUG");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.conn","DEBUG");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.client","DEBUG");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client", "DEBUG");

    }
}
