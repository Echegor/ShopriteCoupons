package com.archelo.coupons;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String [] args){
        System.out.println("Running");
        HTTPPoster poster = new HTTPPoster();
        HttpResponse getResponse = poster.doGet(ShopriteURLS.SIGN_ON);
        String entity = poster.printResponse(getResponse,System.out);
        String postToken = poster.getVerificationToken(entity);
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("__RequestVerificationToken", postToken));
        params.add(new BasicNameValuePair("Email", "xarchelo@live.com"));
        params.add(new BasicNameValuePair("Password", "luis2303"));
        params.add(new BasicNameValuePair("StoreGroupId", "3601"));
        HttpResponse postResponse = poster.doPost(ShopriteURLS.AUTHENTICATION,params);
        poster.printResponse(postResponse,System.out);
    }


}
