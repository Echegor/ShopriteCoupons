package com.archelo.coupons;


import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        System.out.println("Running");

        Utils.setDebugProperties();
        HTTPHandler handler = new HTTPHandler(HeaderUtils.getStandardHeaders());

        String quickSignInEntity = Utils.doGet(handler, ShopriteURLS.QUICK_SIGN_IN, "QUICK_SIGN_IN.html", HeaderUtils.getBasicHeaders());

        String samlForm = Utils.getSamlRequestForm(quickSignInEntity);
        String relayState = Utils.getRelayState(quickSignInEntity);

        ArrayList<NameValuePair> samlQueryParams = new ArrayList<>();
        ArrayList<NameValuePair> samlQueryData = new ArrayList<>();
        samlQueryParams.add(new BasicNameValuePair("binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
        samlQueryParams.add(new BasicNameValuePair("cancelUri", "https://shop.shoprite.com/store/DFA0738/User/QuickSignIn?success=False"));
        samlQueryData.add(new BasicNameValuePair("SAMLRequest", samlForm));
        samlQueryData.add(new BasicNameValuePair("RelayState", relayState));


        String authenticate3601 = Utils.doPost(handler, ShopriteURLS.AUTHENTICATE3601, "AUTHENTICATE3601.html", HeaderUtils.getAuthenticationHeaders3601(), samlQueryParams, samlQueryData);

        String postToken = Utils.getVerificationToken(authenticate3601);
        ArrayList<NameValuePair> authenticationInfo = HeaderUtils.getAuthenticationInfo(postToken);
        String authenticate = Utils.doPost(handler, ShopriteURLS.AUTHENTICATE, "AUTHENTICATE.html", HeaderUtils.getAuthenticationHeaders(), null, authenticationInfo);


        ArrayList<NameValuePair> signedInSamlQueryData = new ArrayList<>();
        signedInSamlQueryData.add(new BasicNameValuePair("SAMLResponse", Utils.getSamlResponseForm(authenticate)));
        signedInSamlQueryData.add(new BasicNameValuePair("RelayState", Utils.getRelayState(authenticate)));
        String signInSuccess = Utils.doPost(handler, ShopriteURLS.SIGN_IN_SUCCESS, "SIGN_IN_SUCCESS.html", HeaderUtils.getSignInSuccessHeaders(), null, signedInSamlQueryData);

        String status = Utils.doGet(handler,ShopriteURLS.STATUS,"STATUS.html",HeaderUtils.getBasicHeaders());
        LoginStatus loginStatus = new Gson().fromJson(status, LoginStatus.class);
        System.out.println("Status " + loginStatus);

        String webJS = Utils.doGet(handler,ShopriteURLS.WEB_JS,"WEB_JS.html",HeaderUtils.getWebJsHeaders());
        AzureTokens azureTokens = Utils.parseWebJSFile(webJS);
        System.out.println("Azure token " + azureTokens);

        String accountInfo = Utils.doGet(handler,ShopriteURLS.STATUS,"STATUS.html",HeaderUtils.getBasicHeaders());
        //String azureSignIn = Utils.doPost(handler, ShopriteURLS.AZURE_SIGN_IN_SUCCESS, "AZURE_SIGN_IN_SUCCESS.html", HeaderUtils.getSignInSuccessHeaders(), null, signedInSamlQueryData);
//        String coupons = doGet(handler,ShopriteURLS.COUPONS,"COUPONS.html",getBasicHeaders());

        //String couponsHome = doGet(handler,ShopriteURLS.COUPONS,"COUPONS.html",getBasicHomeHeaders());
//        System.out.println("Performing GET " + ShopriteURLS.COUPONS);
//        HttpResponse couponGet = handler.doGet(ShopriteURLS.COUPONS);
//        handler.printResponse(couponGet,System.out);
//
//        System.out.println("Performing POST " + ShopriteURLS.COUPON_QUERY);
//        JsonObject priceplus = new JsonObject();
//        priceplus.addProperty("ppc_number", "47110577306");
//        HttpResponse couponListReponse = handler.doJsonEncodedPost(ShopriteURLS.COUPON_QUERY, priceplus);
//        handler.printResponse(couponListReponse, System.out, true);
    }
}
