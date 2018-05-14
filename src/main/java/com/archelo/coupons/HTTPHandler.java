package com.archelo.coupons;

import com.google.gson.JsonObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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



    public static String convertEntityToString(HttpEntity entity) throws IOException {
        if (entity == null) {
            throw new IOException("null entity");
        }

        //try (InputStream inputStream = entity.getContent()) {
            long len = entity.getContentLength();
            if (len != -1 && len < Integer.MAX_VALUE) {
                return EntityUtils.toString(entity);
            } else {
                return convertInputStreamToString(entity.getContent());
            }
        //}

    }

    private static String convertInputStreamToString(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException ioe) {
            return new Date() + "Stream has experienced an io exception " + ioe.toString();
        }

    }

    public static String getSamlRequestForm(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=SAMLRequest]").first().val();
    }

    public static String getSamlResponseForm(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=SAMLResponse]").first().val();
    }

    public static String getRelayState(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=RelayState]").first().val();
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

    private static String toURLEncodedString(ArrayList<NameValuePair> list, boolean isData) {
        if (list == null || list.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;

        for (NameValuePair item : list) {

            String key = URLEncoder.encode(item.getName(), StandardCharsets.UTF_8);
            String value = URLEncoder.encode(item.getValue(), StandardCharsets.UTF_8);

            if (isFirst) {
                builder.append(key).append("=").append(value);
                isFirst = false;
            } else {
                builder.append("&").append(key).append("=").append(value);
            }

        }
        if (!isData) {
            return "?" + builder.toString();
        }
        return builder.toString();
    }

    public HttpResponse doURLEncodedPost(String url, List<Header> headers, ArrayList<NameValuePair> queryData, ArrayList<NameValuePair> data) {
        try {
            String queryParams = toURLEncodedString(queryData, false);
            String dataParam = toURLEncodedString(data, true);

            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url + queryParams);
            httppost.setEntity(new StringEntity(dataParam, ContentType.APPLICATION_FORM_URLENCODED));
            headers.forEach(httppost::addHeader);
            logRequest(httppost);
            return httpclient.execute(httppost, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpResponse doJsonEncodedPost(String url, JsonObject params) {
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

    public String printResponse(HttpResponse response, OutputStream outputStream, boolean printEntity) {
        StringBuilder builder = new StringBuilder().append("Response:").append(System.lineSeparator());
        StatusLine statusLine = response.getStatusLine();
        builder.append("Status: ").append(statusLine.toString()).append(System.lineSeparator());
        builder.append("Response Headers: ");
        builder.append(beautifyHeaders(response.getAllHeaders()));

        String entity = null;
        try {
            entity = convertEntityToString(response.getEntity());
            if (printEntity) {
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

    public HttpResponse doURLEncodedPostWithQuestions(String url, List<Header> headers, ArrayList<NameValuePair> queryData, ArrayList<NameValuePair> data) {
        try {
            String queryParams = toURLEncodedStringWithQuestions(queryData, false);
            String dataParam = toURLEncodedString(data, true);
//            url = url+queryParams;
            url = "";
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new StringEntity(dataParam));
            headers.forEach(httppost::addHeader);
            logRequest(httppost);
            return httpclient.execute(httppost, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String toURLEncodedStringWithQuestions(ArrayList<NameValuePair> list, boolean encode) {
        if (list == null || list.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();

        for (NameValuePair item : list) {
            String key = item.getName();
            String value = item.getValue();
            if (encode) {
                key = URLEncoder.encode(key, StandardCharsets.UTF_8);
                value = URLEncoder.encode(value, StandardCharsets.UTF_8);
            }


            builder.append("?").append(key).append("=").append(value);


        }

        return builder.toString();


    }
}
