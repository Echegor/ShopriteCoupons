package com.archelo.coupons;


import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class Main {
    public static void main(String [] args){
        System.out.println("Running");
        HTTPPoster poster = HTTPPoster.getInstance();
        HttpResponse response = poster.doGet(ShopriteURLS.HOME);
        printResponse(response);
        // Request parameters and other properties.
/*        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("param-1", "12345"));
        params.add(new BasicNameValuePair("param-2", "Hello!"));*/
//        printResponse(poster.doGet(ShopriteURLS.HOME));
    }

    public static void printResponse(HttpResponse response){
        StatusLine statusLine = response.getStatusLine();
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
