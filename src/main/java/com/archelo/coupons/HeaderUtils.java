package com.archelo.coupons;

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


//        headers.add(new BasicHeader("Referer", ShopriteURLS.QUICK_SIGN_IN));
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

    public static ArrayList<NameValuePair> getAuthenticationInfo(String token) {
        ArrayList<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("__RequestVerificationToken", token));
        list.add(new BasicNameValuePair("Email", "xarchelo@live.com"));
        list.add(new BasicNameValuePair("Password", "luis2303"));
        list.add(new BasicNameValuePair("StoreGroupId", "3601"));
        list.add(new BasicNameValuePair("ReturnUrl", ""));
        return list;
    }

    public static ArrayList<Header> getWebJsHeaders() {
        ArrayList<Header> list = new ArrayList<>();
        list.add(new BasicHeader("Referer", "http://coupons.shoprite.com/"));
        return list;
    }

    public static ArrayList<Header> getAzureUserInfo(){
//        headers.add(new BasicHeader("Host", "shop.shoprite.com"));
//        headers.add(new BasicHeader("Origin", "https://secure.shoprite.com"));
//        headers.add(new BasicHeader("Referer", "https://secure.shoprite.com/Account/DoneEditing/3601"));
        return null;
    }
//        Accept: application/json, text/plain, */*
//Accept-Encoding: gzip, deflate, br
//Accept-Language: en-US,en;q=0.5
//Connection: keep-alive
//Host: wfsso.azurewebsites.net
//Origin: http://coupons.shoprite.com
//Referer: http://coupons.shoprite.com/

}
