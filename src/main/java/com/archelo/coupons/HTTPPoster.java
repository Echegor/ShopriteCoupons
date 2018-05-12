package com.archelo.coupons;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPPoster {
    private static HTTPPoster ourInstance = new HTTPPoster();

    public static HTTPPoster getInstance() {
        return ourInstance;
    }

    private HTTPPoster() {
    }

    public HttpResponse doGet(String url){
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            return  httpclient.execute(httpGet);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpResponse doPost(String url, ArrayList<NameValuePair> params){

        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            return httpclient.execute(httppost);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String convertToString(HttpEntity entity) throws IOException {
        if(entity == null){
            throw new IOException("null entity");
        }

        try (InputStream inputStream = entity.getContent()){
            long len = entity.getContentLength();
            if (len != -1 && len < Integer.MAX_VALUE) {
                return EntityUtils.toString(entity);
            } else {
                convertInputStreamToString(inputStream);
            }
        }

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null){
            builder.append(line);
        }
        return  builder.toString();
    }

/*    private String toURLEncodedString(HashMap<String,String> map) {
        if (map.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();

        boolean needsQuestion = true;
        for (Map.Entry<String, String> item : map.entrySet()) {

            String key = URLEncoder.encode(item.getKey(), StandardCharsets.UTF_8);
            String value = URLEncoder.encode(item.getValue(), StandardCharsets.UTF_8);

            if (needsQuestion) {
                needsQuestion = false;
                builder.append("?").append(key).append("=").append(value);
            } else {
                builder.append("&").append(key).append("=").append(value);
            }

        }
        return builder.toString();
    }*/
}
