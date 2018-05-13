package com.archelo.coupons;


import org.apache.http.*;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String [] args)  {
        System.out.println("Running");
        HTTPHandler poster = new HTTPHandler();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,application/json, text/javascript,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Encoding","Accept-Encoding"));
        headers.add(new BasicHeader("Accept-Language","en-US,en;q=0.9"));
        headers.add(new BasicHeader("Connection","keep-alive"));
        //headers.add(new BasicHeader("Cache-Control","max-age=0"));

        headers.add(new BasicHeader("Upgrade-Insecure-Requests","1"));
        headers.add(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36"));

        HttpResponse homeResponse = poster.doGet(ShopriteURLS.HOME,headers);
        poster.printResponse(homeResponse,System.out,true);

        //headers.add(new BasicHeader("Host","shop.shoprite.com"));
        //headers.add(new BasicHeader("Referer","https://www.shoprite.com/"));
        HttpResponse signOnResponse = poster.doGet(ShopriteURLS.SIGN_IN,headers);
        String entity = poster.printResponse(signOnResponse,System.out,true);
        try {
            Files.write(Paths.get("signin.html"),entity.getBytes(),StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String postToken = poster.getVerificationToken(entity);
//        ArrayList<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("__RequestVerificationToken", postToken));
//        params.add(new BasicNameValuePair("Email", "xarchelo@live.com"));
//        params.add(new BasicNameValuePair("Password", "luis2303"));
//        params.add(new BasicNameValuePair("StoreGroupId", "3601"));
//        params.add(new BasicNameValuePair("ReturnUrl", "https://www.shoprite.com/"));
//        HttpResponse postResponse = poster.doURLEncodedPost(ShopriteURLS.AUTHENTICATION,headers,params);
//        poster.printResponse(postResponse,System.out,true);
//
//        HttpResponse landingPage = poster.doGet(ShopriteURLS.SIGNED_IN,headers);
//        poster.printResponse(landingPage,System.out,false);

//        System.out.println("Performing GET " + ShopriteURLS.COUPONS);
//        HttpResponse couponGet = poster.doGet(ShopriteURLS.COUPONS);
//        poster.printResponse(couponGet,System.out);
//
//        System.out.println("Performing POST " + ShopriteURLS.COUPON_QUERY);
//        JsonObject priceplus = new JsonObject();
//        priceplus.addProperty("ppc_number","47110577306");
//        HttpResponse couponListReponse = poster.doJsonEncodedPort(ShopriteURLS.COUPON_QUERY,priceplus);
//        poster.printResponse(couponListReponse,System.out);
    }


}
