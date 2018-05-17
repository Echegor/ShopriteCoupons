package com.archelo.coupons.utils;

import com.archelo.coupons.json.AzureToken;
import com.archelo.coupons.json.LoginStatus;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class HeaderUtils {
    public static List<Header> getBasicHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "shop.shoprite.com"));
        return headers;
    }


    public static List<Header> getStatus() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "shop.shoprite.com"));
        headers.add(new BasicHeader("Origin", "http://coupons.shoprite.com"));
        headers.add(new BasicHeader("Referer", "http://coupons.shoprite.com/"));
        return headers;
    }


    public static List<Header> getStandardHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,application/json, text/javascript,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
        headers.add(new BasicHeader("Accept-Language", "en-US,en;q=0.9"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
        return headers;
    }


    public static List<Header> getAuthenticationHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "secure.shoprite.com"));
        headers.add(new BasicHeader("Origin", "https://secure.shoprite.com"));
        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/User/SignIn/3601"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        return headers;
    }

    public static List<Header> getSignInSuccessHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "shop.shoprite.com"));
        headers.add(new BasicHeader("Origin", "https://secure.shoprite.com"));
        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/Account/DoneEditing/3601"));
        return headers;
    }

    public static List<Header> getAuthenticationHeaders3601() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "secure.shoprite.com"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Referer", "https://shop.shoprite.com/store/DFA0738/User/QuickSignIn"));
        return headers;
    }

    public static List<Header> getAzureAuthenticationHeaders3601(String status) {
        LoginStatus loginStatus = new Gson().fromJson(status, LoginStatus.class);
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "secure.shoprite.com"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Referer", "https://wfsso.azurewebsites.net/SRSSO/SignIn?sessId=" + loginStatus.getUserId()+ "&returnUrl=http://coupons.shoprite.com/"));
        return headers;
    }


    public static ArrayList<Header> getWebJsHeaders() {
        ArrayList<Header> list = new ArrayList<>();
        list.add(new BasicHeader("Referer", "http://coupons.shoprite.com/"));
        return list;
    }

    public static List<Header> getAzureSignInStatus() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "wfsso.azurewebsites.net"));
        headers.add(new BasicHeader("Referer", "http://coupons.shoprite.com/"));
        return headers;
    }

    public static List<Header> getReturnFromSignInHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "wfsso.azurewebsites.net"));
        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/Account/DoneEditing/3601"));
        return headers;
    }

    public static List<Header> getReturnFromSignIn() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept", "application/json, text/plain, */*"));
        headers.add(new BasicHeader("Host", "wfsso.azurewebsites.net"));
        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/Account/DoneEditing/3601"));
        headers.add(new BasicHeader("Origin", "https://secure.shoprite.com"));
        return headers;
    }

    public static List<Header> getAzureStatus() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "wfsso.azurewebsites.net"));
        headers.add(new BasicHeader("Accept", "application/json, text/plain, */*"));
        headers.add(new BasicHeader("Referer", "http://coupons.shoprite.com/"));
        headers.add(new BasicHeader("Origin", "http://coupons.shoprite.com/"));
        return headers;
    }

    public static List<Header> getAvailableCoupons(AzureToken azureToken) {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Host", "stagingcouponswest.azure-mobile.net"));
        headers.add(new BasicHeader("Accept", "*/*"));
        headers.add(new BasicHeader("Referer", "http://coupons.shoprite.com/"));
        headers.add(new BasicHeader("Origin", "http://coupons.shoprite.com"));
        headers.add(new BasicHeader("X-ZUMO-APPLICATION", azureToken.getZumoApplicationToken()));
        //headers.add(new BasicHeader("X-ZUMO-INSTALLATION-ID", "0517ceeb-1a24-4e0d-e763-641105f4f23b"));
        headers.add(new BasicHeader("X-ZUMO-VERSION", "ZUMO/1.0 (lang=Web; os=--; os_version=--; arch=--; version=1.0.20702.0)"));


        return headers;
    }
//        Accept: application/json, text/plain, */*
//Accept-Encoding: gzip, deflate, br
//Accept-Language: en-US,en;q=0.5
//Connection: keep-alive
//Host: wfsso.azurewebsites.net
//Origin: http://coupons.shoprite.com
//Referer: http://coupons.shoprite.com/

}
