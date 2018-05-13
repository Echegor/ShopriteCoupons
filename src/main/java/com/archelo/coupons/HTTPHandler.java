package com.archelo.coupons;

import com.google.gson.JsonObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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
public class HTTPHandler {
    HttpClientContext context;
    CookieStore cookieStore;

    public HTTPHandler() {
        //RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build();
        cookieStore = new BasicCookieStore();
        context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
    }

    public HttpResponse doGet(String url,List<Header> headers) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            headers.forEach(httpGet::addHeader);
            logRequest(httpGet);

            return httpclient.execute(httpGet, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpResponse doURLEncodedPost(String url, List<Header> headers, ArrayList<NameValuePair> params) {

        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            headers.forEach(httppost::addHeader);
            logRequest(httppost);
            return httpclient.execute(httppost, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpResponse doJsonEncodedPort(String url , JsonObject params){
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new StringEntity(
                    params.toString(),
                    ContentType.APPLICATION_JSON));
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

    public String printResponse(HttpResponse response, OutputStream outputStream, boolean printEntity) {
        StringBuilder builder = new StringBuilder();
        StatusLine statusLine = response.getStatusLine();
        builder.append("Status: ").append(statusLine.toString()).append(System.lineSeparator());
        builder.append("Response Headers: ");
        builder.append(beautifyHeaders(response.getAllHeaders()));

        String entity = null;
        try {
            if(printEntity){
                entity = convertEntityToString(response.getEntity());
                builder.append(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        builder.append(System.lineSeparator())
                .append("Cookies: ")
                .append(beautifyCookies(context.getCookieStore()))
                .append(System.lineSeparator())
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

    private void logRequest(HttpPost post){
        System.out.println("POST request " + post.toString());
        System.out.println("POST Headers " + beautifyHeaders(post.getAllHeaders()));
    }

    private void logRequest(HttpGet get){
        System.out.println("GET request " + get.toString());
        System.out.println("GET Headers " + beautifyHeaders(get.getAllHeaders()));
    }

    private String beautifyHeaders(Header[] headers){
        StringBuilder builder = new StringBuilder();
        Arrays.stream(headers).forEach(i->builder.append(i.toString()).append(System.lineSeparator()));
        return builder.toString();
    }

}
