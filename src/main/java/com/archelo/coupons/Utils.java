package com.archelo.coupons;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    public static String getVerificationToken(String string) {
        Document jDoc = Jsoup.parse(string);
        return jDoc.select("input[name=__RequestVerificationToken]").first().val();
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
    public static void writeToFile(String file, String entity) {
        try {
            Files.write(Paths.get("logs", file), entity.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String doPost(HTTPHandler handler, String url, String filename, List<Header> headers, ArrayList<NameValuePair> queryParam, ArrayList<NameValuePair> dataParams) {
        System.out.println(new Date() + " POSTING");
        HttpResponse response = handler.doURLEncodedPost(url, headers, queryParam, dataParams);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }


    public static String doGet(HTTPHandler handler, String url, String filename, List<Header> headers) {
        System.out.println(new Date() + " GETTING");
        HttpResponse response = handler.doGet(url, headers);
        String entity = handler.printResponse(response, System.out, false);
        writeToFile(filename, entity);
        return entity;
    }

    public static AzureTokens parseWebJSFile(String webJSFile){
        int index = webJSFile.indexOf("Authorization");
        int endIndex = webJSFile.indexOf("&",index);
        return new AzureTokens(webJSFile.substring(index+14,endIndex));
    }

    public static void setDebugProperties() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http","DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.conn","DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.client","DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client", "DEBUG");

    }
}
