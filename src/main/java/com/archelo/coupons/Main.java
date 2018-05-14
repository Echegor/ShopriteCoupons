package com.archelo.coupons;


import com.google.gson.JsonObject;
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
import java.util.List;


public class Main {
    public static void main(String[] args) {
        System.out.println("Running");

        setDebugProperties();
        HTTPHandler handler = new HTTPHandler();

        String quickSignInEntity = doGet(handler, ShopriteURLS.QUICK_SIGN_IN, "QUICK_SIGN_IN.html", getBasicHeaders());

        String samlForm = HTTPHandler.getSamlRequestForm(quickSignInEntity);
        String relayState = HTTPHandler.getRelayState(quickSignInEntity);

        ArrayList<NameValuePair> samlQueryParams = new ArrayList<>();
        ArrayList<NameValuePair> samlQueryData = new ArrayList<>();
        samlQueryParams.add(new BasicNameValuePair("binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
        samlQueryParams.add(new BasicNameValuePair("cancelUri", "https://www.shoprite.com/?store=DFA0738"));
        samlQueryData.add(new BasicNameValuePair("SAMLRequest", samlForm));
        samlQueryData.add(new BasicNameValuePair("RelayState", relayState));

        String authenticate3601 = doPost(handler, ShopriteURLS.AUTHENTICATE3601, "AUTHENTICATE3601.html", getAuthenticationHeaders3601(), samlQueryParams, samlQueryData);

        String signIn = doGet(handler, ShopriteURLS.SIGN_IN, "SIGN_IN.html", getAuthenticationHeaders3601());

        String postToken = handler.getVerificationToken(signIn);
        ArrayList<NameValuePair> authenticationInfo = getAuthenticationInfo(postToken);
        String authenticate = doPost(handler, ShopriteURLS.AUTHENTICATE, "AUTHENTICATE.html", getDoneEditingHeaders(), null, authenticationInfo);

//        List<Header> doneEditingHeaders = getBasicHeaders();
//        doneEditingHeaders.add(new BasicHeader())
        String signedInQuery = doGet(handler, ShopriteURLS.DONE_EDITING, "SIGN_IN_QUERY.html", getDoneEditingHeaders());

        ArrayList<NameValuePair> signedInSamlQueryParams = new ArrayList<>();
        signedInSamlQueryParams.add(new BasicNameValuePair("success", "True"));
        signedInSamlQueryParams.add(new BasicNameValuePair("binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
        ArrayList<NameValuePair> signedInSamlQueryData = new ArrayList<>();
        signedInSamlQueryData.add(new BasicNameValuePair("SAMLRequest", HTTPHandler.getSamlResponseForm(signedInQuery)));
        signedInSamlQueryData.add(new BasicNameValuePair("RelayState", HTTPHandler.getRelayState(signedInQuery)));

        String homeTest = doGet(handler, ShopriteURLS.HOME, "HOME.html", getBasicHomeHeaders());
        String signInSuccess = doPost(handler, ShopriteURLS.SIGN_IN_SUCCESS, "SIGN_IN_SUCCESS.html", getAuthenticationHeaders(), null, signedInSamlQueryData);

        //String couponsHome = doGet(handler,ShopriteURLS.COUPONS,"COUPONS.html",getBasicHomeHeaders());
//        System.out.println("Performing GET " + ShopriteURLS.COUPONS);
//        HttpResponse couponGet = handler.doGet(ShopriteURLS.COUPONS);
//        handler.printResponse(couponGet,System.out);
//
        System.out.println("Performing POST " + ShopriteURLS.COUPON_QUERY);
        JsonObject priceplus = new JsonObject();
        priceplus.addProperty("ppc_number", "47110577306");
        HttpResponse couponListReponse = handler.doJsonEncodedPost(ShopriteURLS.COUPON_QUERY, priceplus);
        handler.printResponse(couponListReponse, System.out, true);
    }

    public static void writeToFile(String file, String entity) {
        try {
            Files.write(Paths.get("logs", file), entity.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String doPost(HTTPHandler handler, String url, String filename, List<Header> headers, ArrayList<NameValuePair> queryParam, ArrayList<NameValuePair> dataParams) {
        HttpResponse response = handler.doURLEncodedPost(url, headers, queryParam, dataParams);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static String doGet(HTTPHandler handler, String url, String filename, List<Header> headers) {
        HttpResponse response = handler.doGet(url, headers);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static void setDebugProperties() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
    }

    public static List<Header> getBasicHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "shop.shoprite.com"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,application/json, text/javascript,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Encoding", "Accept-Encoding"));
        headers.add(new BasicHeader("Accept-Language", "en-US,en;q=0.9"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36"));

//        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
//        headers.add(new BasicHeader("Referer", ShopriteURLS.QUICK_SIGN_IN));
        return headers;
    }

    public static List<Header> getBasicHomeHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "shop.shoprite.com"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,application/json, text/javascript,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Encoding", "Accept-Encoding"));
        headers.add(new BasicHeader("Accept-Language", "en-US,en;q=0.9"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36"));
        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/Account/DoneEditing/3601"));
        return headers;
    }

    public static List<Header> getAuthenticationHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "shop.shoprite.com"));
        headers.add(new BasicHeader("Origin", "https://secure.shoprite.com"));
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Language", "en-US,en;q=0.5"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/Account/DoneEditing/3601"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
        return headers;
    }

    public static List<Header> getAuthenticationHeaders3601() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "secure.shoprite.com"));
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Language", "en-US,en;q=0.5"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
        headers.add(new BasicHeader("Referer", "https://shop.shoprite.com/store/DFA0738/User/QuickSignIn"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
        return headers;
    }

    public static List<Header> getDoneEditingHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "secure.shoprite.com"));
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Language", "en-US,en;q=0.5"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/User/SignIn/3601"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
        return headers;
    }

    public static ArrayList<NameValuePair> getAuthenticationInfo(String token) {
        ArrayList<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("__RequestVerificationToken", token));
        list.add(new BasicNameValuePair("Email", "xarchelo@live.com"));
        list.add(new BasicNameValuePair("Password", "luis2303"));
        list.add(new BasicNameValuePair("StoreGroupId", "3601"));
        list.add(new BasicNameValuePair("ReturnUrl", "https://www.shoprite.com/"));
        return list;
    }


}
