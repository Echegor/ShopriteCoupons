package com.archelo.coupons;

import com.google.gson.JsonObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*Preserves session across post and get request*/

//TODO properly release connection. Program hangs if setMaxConnPerRoute(100000) is not set
public class HTTPHandler {
    HttpClientContext context;
    CookieStore cookieStore;
    List<Header> defaultHeaders;
    HttpClient client;

    public HTTPHandler(List<Header> defaultHeaders) {
        HttpHost proxy = new HttpHost("10.120.30.19", 8080, "http");
        RequestConfig globalConfig = RequestConfig
                .custom()
                .setProxy(proxy)
                .setCircularRedirectsAllowed(false)
                .build();
        cookieStore = new BasicCookieStore();
        context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        context.setRequestConfig(globalConfig);
        context.setAuthCache(new BasicAuthCache());
        this.defaultHeaders = defaultHeaders;
        client = createDefaultClient();
    }
    private HttpClient getDefaultClient (){
        return client;
    }

    private HttpClient createDefaultClient (){
        return HttpClientBuilder.create()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0")
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultHeaders(defaultHeaders)
                .setMaxConnPerRoute(100000)
                .build();
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

    public HttpResponse doGet(String url,List<Header> headers) {
        try {
            HttpClient httpclient = getDefaultClient();
            HttpGet httpGet = new HttpGet(url);
            headers.forEach(httpGet::addHeader);
            logRequest(httpGet);

            return httpclient.execute(httpGet, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

    private static String toURLEncodedString(List<NameValuePair> list, boolean isData, boolean encode) {
        if (list == null || list.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;

        for (NameValuePair item : list) {

            String key = null;
            String value = null;
            try {
                if (encode) {
                    key = URLEncoder.encode(item.getName(), "UTF-8");
                    value = URLEncoder.encode(item.getValue(), "UTF-8");
                } else {
                    key = item.getName();
                    value = item.getValue();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


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

    private static String beautifyCookies(CookieStore cookies) {
        List<Cookie> list = cookies.getCookies();
        StringBuilder builder = new StringBuilder();
        for (Cookie cookie : list) {
            builder.append(cookie.toString()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    public HttpResponse doURLUenncodedGet(String url, List<Header> headers, List<NameValuePair> queryParams) {
        try {
            String query = toURLEncodedString(queryParams, false, false);
            HttpClient httpclient = getDefaultClient();
            HttpGet httpGet = new HttpGet(url + query);
            headers.forEach(httpGet::addHeader);
            logRequest(httpGet);

            return httpclient.execute(httpGet, context);

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

    public HttpResponse doURLEncodedGet(String url, List<Header> headers, List<NameValuePair> queryParams) {
        try {
            String query = toURLEncodedString(queryParams, false, true);
            HttpClient httpclient = getDefaultClient();
            HttpGet httpGet = new HttpGet(url + query);
            headers.forEach(httpGet::addHeader);
            logRequest(httpGet);

            return httpclient.execute(httpGet, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpResponse doURLUnencodedPost(String url, List<Header> headers, List<NameValuePair> queryData, List<NameValuePair> data) {
        try {
            String queryParams = toURLEncodedString(queryData, false, false);
            String dataParam = toURLEncodedString(data, true, true);

            HttpClient httpclient = getDefaultClient();
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

    public HttpResponse doURLEncodedPost(String url, List<Header> headers, List<NameValuePair> queryData, List<NameValuePair> data) {
        try {
            String queryParams = toURLEncodedString(queryData, false,true);
            String dataParam = toURLEncodedString(data, true,true);

            HttpClient httpclient = getDefaultClient();
            HttpPost httppost = new HttpPost(url + queryParams);
            httppost.setEntity(new StringEntity(dataParam,ContentType.APPLICATION_FORM_URLENCODED));

            headers.forEach(httppost::addHeader);
            logRequest(httppost);
            return httpclient.execute(httppost, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpResponse doJsonEncodedPost(String url, List<Header> headers, JsonObject params) {
        try {
            HttpClient httpclient = getDefaultClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new StringEntity(
                    params.toString(),
                    ContentType.APPLICATION_JSON));

            headers.forEach(httppost::addHeader);
            return httpclient.execute(httppost, context);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void logRequest(HttpPost post) {
        System.out.println("POST request " + post.toString());
        System.out.println("POST Headers " + beautifyHeaders(post.getAllHeaders()));
    }

    private void logRequest(HttpGet get) {
        System.out.println("GET request " + get.toString());
        System.out.println("GET Headers " + beautifyHeaders(get.getAllHeaders()));
    }

    private String beautifyHeaders(Header[] headers) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(headers).forEach(i -> builder.append(i.toString()).append(System.lineSeparator()));
        return builder.toString();
    }


}
