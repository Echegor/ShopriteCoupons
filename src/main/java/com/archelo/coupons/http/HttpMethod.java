package com.archelo.coupons.http;

import com.archelo.coupons.utils.HeaderUtils;
import com.google.gson.JsonObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.archelo.coupons.utils.AgentUtils.MOZILLA_AGENT;

public abstract class HttpMethod {
    protected String url;
    protected List<Header> headers;
    protected List<NameValuePair> queryParams;
    protected List<NameValuePair> dataParams;
    protected HttpClient httpClient;
    protected HttpClientContext httpClientContext;
    protected boolean encodeQueryParams;
    protected boolean encodeDataParams;
    protected String endodedQueryParams;
    protected String endodedDataParams;
    protected ContentType contentType;
    protected JsonObject jsonObject;

    protected static HttpClient createDefaultClient() {
        return HttpClientBuilder.create()
                .setUserAgent(MOZILLA_AGENT)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultHeaders(HeaderUtils.getStandardHeaders())
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

    private String beautifyHeaders(Header[] headers) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(headers).forEach(i -> builder.append(i.toString()).append(System.lineSeparator()));
        return builder.toString();
    }

    protected String printCompleteResponse(HttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder().append("Response:").append(System.lineSeparator());
        StatusLine statusLine = response.getStatusLine();
        builder.append("Status: ").append(statusLine.toString()).append(System.lineSeparator());
        builder.append("Response Headers: ");
        builder.append(beautifyHeaders(response.getAllHeaders()));
        HttpEntity responseEntity = response.getEntity();
        String entity = convertEntityToString(responseEntity);
        builder.append(entity);

        builder.append(System.lineSeparator()).append("Cookies: ");
        if (httpClientContext != null) {
            builder.append(beautifyCookies(httpClientContext.getCookieStore()));
        }

        builder.append(System.lineSeparator())
                .append(System.lineSeparator());

        EntityUtils.consume(responseEntity);

        return entity;
    }

    protected String printResponse(HttpResponse response) throws IOException {
        HttpEntity responseEntity = response.getEntity();
        String entity = convertEntityToString(responseEntity);

        EntityUtils.consume(responseEntity);
        return entity;
    }

    protected static String toURLEncodedString(List<NameValuePair> list, boolean isData, boolean encode) {
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

    protected static String beautifyCookies(CookieStore cookies) {
        List<Cookie> list = cookies.getCookies();
        StringBuilder builder = new StringBuilder();
        for (Cookie cookie : list) {
            builder.append(cookie.toString()).append(System.lineSeparator());
        }
        return builder.toString();
    }
}
