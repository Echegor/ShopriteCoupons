package com.archelo.coupons;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*Preserves session across post and get request*/
public class HTTPPoster {
    HttpClientContext context;
    CookieStore cookieStore;

    public HTTPPoster() {
        //RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build();
        cookieStore = new BasicCookieStore();
        context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
    }

    public HttpResponse doGet(String url) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            return httpclient.execute(httpGet, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpResponse doPost(String url, ArrayList<NameValuePair> params) {

        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            return httpclient.execute(httppost, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String convertEntityToString(HttpEntity entity) throws IOException {
        if (entity == null) {
            throw new IOException("null entity");
        }

        try (InputStream inputStream = entity.getContent()) {
            long len = entity.getContentLength();
            if (len != -1 && len < Integer.MAX_VALUE) {
                return EntityUtils.toString(entity);
            } else {
                return convertInputStreamToString(inputStream);
            }
        }

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    public String printResponse(HttpResponse response, OutputStream outputStream) {
        StringBuilder builder = new StringBuilder();
        StatusLine statusLine = response.getStatusLine();
        builder.append("Status: ").append(statusLine.toString()).append(System.lineSeparator());
        builder.append("Response Headers: ");
        Arrays.stream(response.getAllHeaders()).forEach(i->builder.append(i.toString()).append(System.lineSeparator()));
        String entity = null;
        try {
            entity = convertEntityToString(response.getEntity());
            builder.append(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        builder.append(System.lineSeparator())
                .append("Cookies: ")
                .append(beautifyCookies(context.getCookieStore()))
                .append(System.lineSeparator());
        try {
            outputStream.write(builder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entity;
    }

    private static String beautifyCookies(CookieStore cookies){
        List<Cookie> list = cookies.getCookies();
        StringBuilder builder = new StringBuilder();
        for(Cookie cookie : list){
            builder.append(cookie.toString()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public String getVerificationToken(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=__RequestVerificationToken]").first().val();
    }

}
